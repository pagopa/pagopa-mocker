# Guida Operativa — pagoPa Mocker

## Indice

1. [Cos'è il Mocker](#cosè-il-mocker)
2. [Architettura del sistema](#architettura-del-sistema)
3. [Avvio locale](#avvio-locale)
4. [Struttura dati su MongoDB](#struttura-dati-su-mongodb)
   - [Mock Resource](#mock-resource)
   - [Regole (Rules)](#regole-rules)
   - [Condizioni (Conditions)](#condizioni-conditions)
   - [Risposta (Response)](#risposta-response)
   - [Scripting](#scripting)
5. [Come viene calcolato l'ID della risorsa](#come-viene-calcolato-lid-della-risorsa)
6. [Come richiamare il Mocker](#come-richiamare-il-mocker)
7. [Logica di matching delle regole](#logica-di-matching-delle-regole)
8. [Iniezione dinamica di parametri nella risposta](#iniezione-dinamica-di-parametri-nella-risposta)
9. [Scripting JavaScript](#scripting-javascript)
10. [Cache Redis](#cache-redis)
11. [Header speciali](#header-speciali)
12. [Endpoint di sistema](#endpoint-di-sistema)
13. [Variabili d'ambiente](#variabili-dambiente)
14. [Esempi completi](#esempi-completi)

---

## Cos'è il Mocker

Il **pagoPa Mocker** è un'applicazione Spring Boot che funge da server HTTP mock dinamico. Intercetta qualsiasi chiamata HTTP e restituisce risposte preconfigurate caricate da MongoDB.

È progettato per simulare le risposte di API reali durante i test, senza dover dipendere dai sistemi target. È particolarmente utile in pipeline CI/CD e ambienti di test dove i sistemi upstream non sono disponibili o non devono essere chiamati.

---

## Architettura del sistema

```
Client HTTP
     │
     ▼
ProxyServlet  (/mocker/*)
     │
     ▼
ProxyService  ──► CacheService (Redis)
     │                  │ cache hit
     ▼                  ▼
MockerService       risposta
     │
     ▼ query su MongoDB
MockResourceRepository
     │
     ▼
ResourceExtractor
  ├── parsing body (JSON / XML / String)
  ├── valutazione condizioni (BODY / HEADER / URL)
  ├── selezione della prima regola che fa match
  └── costruzione risposta (con parametri iniettati + scripting JS)
```

**Componenti principali:**

| Componente | Ruolo |
|---|---|
| `ProxyServlet` | Entry point HTTP, registrato su `/mocker/*` |
| `ProxyService` | Orchestrazione: prima controlla la cache Redis, poi chiama `MockerService` |
| `MockerService` | Recupera la `MockResource` da MongoDB e delega l'analisi |
| `ResourceExtractor` | Fa il parse del body, valuta le condizioni, costruisce la risposta |
| `ScriptExecutor` | Esegue script JavaScript (Nashorn) per produrre valori dinamici |
| `CacheService` | Gestisce la cache Redis con TTL di 5 giorni |

**Database utilizzati:**

- **MongoDB** — contiene `mock_resources` e `scripts`
- **Redis** — cache opzionale delle risposte già calcolate

---

## Avvio locale

### Prerequisiti

- Docker
- Java 17 + Maven (solo per sviluppo)

### Avvio via Docker

```bash
cd ./docker
sh ./run_docker.sh dev
```

> Per PagoPa ACR è richiesto il login preventivo: `az acr login -n <acr-name>`

### Avvio via Maven (sviluppo)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Il profilo `local` usa la porta **8081** e punta a database configurati tramite variabili d'ambiente.

---

## Struttura dati su MongoDB

### Mock Resource

Ogni risorsa mockata è un documento nella collection **`mock_resources`**.

```json
{
  "_id": "<hash MD5>",
  "name": "Descrizione human-readable",
  "subsystemUrl": "ec-service/api/v1",
  "resourceUrl": "organizations/77777777777",
  "httpMethod": "POST",
  "specialHeaders": [],
  "isActive": true,
  "rules": [ ... ]
}
```

| Campo | Tipo | Descrizione |
|---|---|---|
| `_id` | String | Hash MD5 calcolato da metodo + URL + special headers (vedi sotto) |
| `name` | String | Nome descrittivo, solo per leggibilità |
| `subsystemUrl` | String | Prefisso del path (es. `ec-service/api/v1`) |
| `resourceUrl` | String | Path della risorsa (es. `organizations/12345`) |
| `httpMethod` | Enum | `GET`, `POST`, `PUT`, `DELETE`, `PATCH` |
| `specialHeaders` | Array | Header aggiuntivi inclusi nel calcolo dell'ID (es. `[{"name":"serviceType","value":"NODO"}]`) |
| `isActive` | Boolean | Se `false`, il Mocker risponde con errore 404 |
| `rules` | Array | Lista delle regole di matching, ordinate per `order` |

---

### Regole (Rules)

Le regole sono contenute dentro ogni `MockResource`. Vengono valutate in ordine crescente di `order`. **Vince la prima regola che ha tutte le condizioni soddisfatte.**

```json
{
  "id": "uuid-regola",
  "name": "Regola principale",
  "order": 1,
  "isActive": true,
  "conditions": [ ... ],
  "response": { ... },
  "scripting": { ... }
}
```

| Campo | Descrizione |
|---|---|
| `order` | Priorità — numero intero crescente; la regola con il numero più basso viene valutata per prima |
| `isActive` | Se `false`, la regola è ignorata |
| `conditions` | Lista di condizioni da rispettare (AND logico tra tutte) |
| `response` | Risposta da restituire se tutte le condizioni sono soddisfatte |
| `scripting` | Script opzionale per generare valori dinamici da iniettare nella risposta |

> **Pattern "Parachute Rule":** è buona pratica inserire sempre una regola con `order: 10000` e `conditions: []`. Questa regola non ha condizioni e quindi fa sempre match, fungendo da risposta di default/fallback.

---

### Condizioni (Conditions)

Ogni condizione verifica un campo della richiesta HTTP:

```json
{
  "id": "uuid-condizione",
  "order": 1,
  "fieldPosition": "BODY",
  "analyzedContentType": "JSON",
  "fieldName": "name",
  "conditionType": "EQ",
  "conditionValue": "fake-ec"
}
```

**`fieldPosition`** — dove cercare il campo:

| Valore | Descrizione |
|---|---|
| `BODY` | Nel corpo della richiesta |
| `HEADER` | Negli header HTTP |
| `URL` | Nei query parameter (`?param=value`) |

**`analyzedContentType`** — come parsare il body (rilevante solo per `fieldPosition: BODY`):

| Valore | Descrizione |
|---|---|
| `JSON` | Body parsato come JSON; `fieldName` è la chiave JSON (es. `organizationId`) |
| `XML` | Body parsato come XML; `fieldName` usa dot-notation (es. `envelope.body.name`) |
| `STRING` | Body trattato come stringa grezza |

**`conditionType`** — operatore di confronto:

| Operatore | Descrizione |
|---|---|
| `EQ` | Uguale al valore |
| `NEQ` | Diverso dal valore |
| `LT` | Minore del valore (numerico) |
| `GT` | Maggiore del valore (numerico) |
| `LE` | Minore o uguale (numerico) |
| `GE` | Maggiore o uguale (numerico) |
| `REGEX` | Soddisfa l'espressione regolare (es. `^pagopa$`) |
| `NULL` | Il campo non è presente / è null |
| `ANY` | Il campo esiste ed è non-null (valore qualsiasi) |
| `TRUE` | Il campo è un booleano `true` |
| `FALSE` | Il campo è un booleano `false` |

> **Nota:** per `HEADER` e `URL`, `analyzedContentType` è ignorato — il valore viene sempre trattato come stringa.

---

### Risposta (Response)

```json
{
  "body": "<stringa Base64>",
  "status": 200,
  "isCacheable": true,
  "headers": [
    { "header": "Content-Type", "value": "application/json" }
  ],
  "parameters": ["name", "organizationId"]
}
```

| Campo | Descrizione |
|---|---|
| `body` | Corpo della risposta codificato in **Base64** |
| `status` | HTTP status code (es. `200`, `404`, `500`) |
| `isCacheable` | Se `true` (o null), la risposta viene salvata in cache Redis |
| `headers` | Header HTTP da aggiungere alla risposta |
| `parameters` | Nomi dei campi del body/query param da iniettare nella risposta (vedi sotto) |

---

### Scripting

Campo opzionale dentro la regola per generare valori dinamici tramite JavaScript:

```json
{
  "scriptName": "calcolaImporto",
  "isActive": true,
  "parameters": [
    { "name": "importo", "value": "${amount}" },
    { "name": "iva", "value": "22" }
  ]
}
```

- `scriptName` — nome della funzione JavaScript da eseguire (deve esistere nella collection `scripts`)
- `parameters` — parametri passati allo script; i valori con `${fieldName}` vengono estratti dal body della richiesta

---

## Come viene calcolato l'ID della risorsa

L'`_id` del documento MongoDB è un hash MD5 calcolato su:

```
MD5( httpMethod + " " + "/" + subsystemUrl + "/" + resourceUrl + "/" + specialHeaders )
```

Esempio per una risorsa `POST /ec-service/api/v1/organizations/77777777777` senza special headers:

```
MD5("post /ec-service/api/v1/organizations/77777777777/ ")
```

> **Attenzione:** il metodo HTTP è in **minuscolo**, l'URL è terminato con `/`, e gli special headers sono ordinati e concatenati con `;`.

Questo ID viene usato come chiave di lookup su MongoDB: se non esiste nessun documento con quell'ID, il Mocker restituisce un errore 404.

---

## Come richiamare il Mocker

Tutte le chiamate al Mocker passano per il prefisso `/mocker/`. La struttura dell'URL è:

```
/mocker/{subsystemUrl}/{resourceUrl}
```

**Esempio:**

Se la `MockResource` ha:
- `subsystemUrl = "ec-service/api/v1"`
- `resourceUrl = "organizations/77777777777"`
- `httpMethod = "POST"`

La chiamata corretta è:

```http
POST http://<host>/mocker/ec-service/api/v1/organizations/77777777777
Content-Type: application/json

{ "name": "fake-ec" }
```

---

## Logica di matching delle regole

Il flusso di esecuzione per ogni richiesta:

```
1. Calcola l'ID risorsa (MD5 di metodo + URL + special headers)
2. Recupera MockResource da MongoDB con quell'ID
   └─► Se non trovata → 404 "Resource not registered"
   └─► Se isActive = false → 404 "Resource not active"
3. Ordina le regole per `order` (ASC)
4. Per ogni regola attiva:
   a. Valuta tutte le conditions (AND logico)
   b. Se TUTTE le condizioni passano → usa questa regola
5. Se nessuna regola fa match → errore "No compliant rule found"
6. Costruisce la risposta dalla regola selezionata
7. Salva in cache Redis (se abilitata e isCacheable = true)
```

> **Regola senza condizioni** (`conditions: []`) fa sempre match — usarla come ultima regola (parachute) garantisce che ci sia sempre una risposta.

---

## Iniezione dinamica di parametri nella risposta

Il `body` della risposta è in Base64 e può contenere placeholder nella forma `${fieldName}`.

Quando `parameters` contiene il nome di un campo:
1. Il Mocker estrae il valore di quel campo dal **body della richiesta** (JSON/XML)
2. Oppure dai **query parameter** dell'URL
3. Sostituisce `${fieldName}` nel body della risposta con il valore trovato

**Esempio:**

Body risposta (decoded):
```json
{
  "organizationName": "${name}",
  "onboardingDate": "2023-06-20T15:03:56"
}
```

Body richiesta:
```json
{ "name": "fake-ec" }
```

Risposta effettiva:
```json
{
  "organizationName": "fake-ec",
  "onboardingDate": "2023-06-20T15:03:56"
}
```

Per i valori generati da script, il placeholder è `${dynamic.<nomeChiave>}`.

---

## Scripting JavaScript

Gli script sono salvati nella collection MongoDB **`scripts`**:

```json
{
  "_id": "<uuid>",
  "name": "calcolaImporto",
  "selectable": true,
  "code": "<base64 del codice JS>"
}
```

| Campo | Descrizione |
|---|---|
| `name` | Identificatore usato in `scriptName` della regola |
| `selectable` | `true` = funzione callable; `false` = libreria helper (caricata prima) |
| `code` | Codice JavaScript codificato in Base64 |

La funzione deve avere questa firma:

```javascript
function execute(params) {
  // params contiene i parametri definiti in scripting.parameters
  var importo = params.importo;
  var iva = params.iva;
  var totale = importo * (1 + iva / 100);
  return {
    "totale": String(totale)
  };
}
```

Il valore di ritorno è una `Map<String, String>` accessibile nella risposta tramite `${dynamic.totale}`.

> Gli script vengono caricati all'avvio dell'applicazione dal Nashorn engine. Se uno script ha errori di sintassi, l'avvio fallisce.

---

## Cache Redis

Il Mocker supporta una cache Redis per evitare di ricalcolare risposte identiche.

- **Abilitazione:** variabile `MOCKER_CACHE_ENABLED=true`
- **TTL:** 5 giorni
- **Chiave primaria:** MD5(httpMethod + URL + specialHeaders) — identifica la risorsa
- **Hash key:** MD5(headers + queryParams + body) — identifica la specifica richiesta

**Header esclusi dalla chiave di cache** (per evitare cache miss inutili):
- `authorization`, `age`, `cache-control`, `etag`, `expires`, `user-agent`
- `traceparent`, `x-appgw-trace-id`, `x-client-ip`, `x-real-ip`, `x-request-id`
- `x-cache-exclude-headers`, `postman-token`, header Sec-*

**Esclusione dinamica di header specifici:**

```http
x-cache-exclude-headers: my-custom-header, another-header
```

Aggiungendo questo header alla richiesta, gli header elencati saranno esclusi dal calcolo della cache key per quella chiamata.

**Disabilitare la cache per una risposta specifica:** impostare `isCacheable: false` nella response.

---

## Header speciali

Gli **special headers** consentono di avere mock diversi per lo stesso URL e metodo HTTP, differenziati dal valore di un header.

Si configurano a livello di sistema tramite `MOCKER_REQUEST_SPECIALHEADERS` (lista separata da virgola).

**Esempio:** se `MOCKER_REQUEST_SPECIALHEADERS=serviceType`, una richiesta con `serviceType: NODO` e una con `serviceType: WISP` vengono trattate come due risorse separate e possono avere mock diversi.

Gli special headers sono inclusi nel calcolo dell'MD5 dell'ID della risorsa.

---

## Endpoint di sistema

### Info

```http
GET /mocker/info
```

Risposta:
```json
{
  "name": "pagopa-mocker",
  "version": "1.3.2",
  "environment": "azure-aks"
}
```

### CORS

Il Mocker aggiunge automaticamente gli header CORS (`Access-Control-Allow-*: *`) se:
- La richiesta è di tipo `OPTIONS`
- L'header `x-source-client` ha un valore presente nella whitelist `MOCKER_ACCEPTED_CLIENTS`

---

## Variabili d'ambiente

| Variabile | Default | Descrizione |
|---|---|---|
| `MONGODB_CONNECTION_URI` | — | URI di connessione a MongoDB |
| `MONGODB_NAME` | `mocker` | Nome del database MongoDB |
| `REDIS_HOST` | — | Hostname Redis |
| `REDIS_PORT` | — | Porta Redis |
| `REDIS_PASSWORD` | — | Password Redis |
| `MOCKER_CACHE_ENABLED` | — | `true` per abilitare la cache Redis |
| `MOCKER_REQUEST_SPECIALHEADERS` | — | Header speciali inclusi nell'ID risorsa (es. `serviceType,channelType`) |
| `MOCKER_ACCEPTED_CLIENTS` | `pagopa-shared-toolbox` | Client autorizzati a ricevere header CORS |
| `ENV` | `azure-aks` | Ambiente applicativo (restituito da `/mocker/info`) |
| `DEFAULT_LOGGING_LEVEL` | `INFO` | Log level root |
| `APP_LOGGING_LEVEL` | `INFO` | Log level applicativo |

---

## Esempi completi

### Esempio 1 — Mock JSON con condizione su body e header

**Documento MongoDB (`mock_resources`):**

```json
{
  "_id": "bce9086e9cf15dd62401b7a440f75610",
  "name": "Cerca ente creditore 77777777777",
  "subsystemUrl": "ec-service/api/v1",
  "resourceUrl": "organizations/77777777777",
  "httpMethod": "POST",
  "specialHeaders": [],
  "isActive": true,
  "rules": [
    {
      "id": "rule-1",
      "name": "Regola principale",
      "order": 1,
      "isActive": true,
      "conditions": [
        {
          "id": "cond-1",
          "order": 1,
          "fieldPosition": "BODY",
          "analyzedContentType": "JSON",
          "fieldName": "name",
          "conditionType": "EQ",
          "conditionValue": "fake-ec"
        },
        {
          "id": "cond-2",
          "order": 2,
          "fieldPosition": "HEADER",
          "analyzedContentType": "STRING",
          "fieldName": "x-client-name",
          "conditionType": "REGEX",
          "conditionValue": "^pagopa$"
        }
      ],
      "response": {
        "body": "ewogICJvcmdhbml6YXRpb25OYW1lIjogIiR7bmFtZX0iCn0=",
        "status": 200,
        "isCacheable": true,
        "headers": [{ "header": "Content-Type", "value": "application/json" }],
        "parameters": ["name"]
      }
    },
    {
      "id": "rule-parachute",
      "name": "Parachute Rule",
      "order": 10000,
      "isActive": true,
      "conditions": [],
      "response": {
        "body": "eyJlcnJvcmUiOiAicmVzb3JzYSBub24gdHJvdmF0YSJ9",
        "status": 404,
        "isCacheable": false,
        "headers": [{ "header": "Content-Type", "value": "application/json" }],
        "parameters": []
      }
    }
  ]
}
```

**Chiamata che fa match con la regola principale:**

```http
POST /mocker/ec-service/api/v1/organizations/77777777777
Content-Type: application/json
x-client-name: pagopa

{
  "name": "fake-ec"
}
```

Il body della risposta (`ewogICJvcmdhbml6YXRpb25OYW1lIjogIiR7bmFtZX0iCn0=` → `{"organizationName": "${name}"}`) avrà `${name}` sostituito con `fake-ec`:

```json
{ "organizationName": "fake-ec" }
```

---

### Esempio 2 — Mock XML con condizione su body XML

**Condizione:**
```json
{
  "fieldPosition": "BODY",
  "analyzedContentType": "XML",
  "fieldName": "body.name",
  "conditionType": "EQ",
  "conditionValue": "fake-ec"
}
```

**Richiesta XML:**
```http
POST /mocker/ec-service/api/v1/organizations/77777777777
Content-Type: application/xml

<envelope>
  <body>
    <name>fake-ec</name>
  </body>
</envelope>
```

Il campo `body.name` (dot-notation sul nodo XML) viene estratto e confrontato con `fake-ec`.

---

### Esempio 3 — Mock con condizione su query parameter

**Condizione:**
```json
{
  "fieldPosition": "URL",
  "analyzedContentType": "STRING",
  "fieldName": "param",
  "conditionType": "EQ",
  "conditionValue": "value"
}
```

**Richiesta:**
```http
GET /mocker/my-service/api/v1/resource?param=value
```

---

### Esempio 4 — Script JavaScript per calcolo dinamico

**Documento `scripts`:**
```json
{
  "_id": "script-uuid-1",
  "name": "calcolaIva",
  "selectable": true,
  "code": "ZnVuY3Rpb24gZXhlY3V0ZShwYXJhbXMpIHsKICB2YXIgaW1wb3J0byA9IHBhcmFtcy5pbXBvcnRvOwogIHZhciB0b3RhbGUgPSBpbXBvcnRvICogMS4yMjsKICByZXR1cm4geyJ0b3RhbGUiOiBTdHJpbmcodG90YWxlKX07Cn0="
}
```

*(il codice decodificato è: `function execute(params) { var importo = params.importo; var totale = importo * 1.22; return {"totale": String(totale)}; }`)*

**Scripting nella regola:**
```json
{
  "scriptName": "calcolaIva",
  "isActive": true,
  "parameters": [
    { "name": "importo", "value": "${amount}" }
  ]
}
```

**Body risposta (decoded):**
```json
{ "importoTotale": "${dynamic.totale}" }
```

Se la richiesta contiene `{ "amount": 100 }`, la risposta sarà:
```json
{ "importoTotale": "122.0" }
```

---

### Come codificare il body in Base64

Da terminale:
```bash
echo -n '{"organizationName": "${name}"}' | base64
```

Da Python:
```python
import base64
body = '{"organizationName": "${name}"}'
print(base64.b64encode(body.encode()).decode())
```
