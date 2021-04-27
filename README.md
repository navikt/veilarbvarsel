# VeilarbVarsel

## Todos:
- Sende melding på 3 forskjellige MQ
- Motta melding på Kafka
- bruke den gamle funksjonen
- Utforske topic `aapen-dok-notifikasjon-status` for notifikasjoner (https://navikt.github.io/brukernotifikasjon-docs/eksternvarsling/)
- Trenger vi lagre?
- Grafana


## Kafka

## Meldingdesign

### Veilarbaktivitet

Varsler:
- Når en aktivitet blir avtalt

Aktiviteter:
- SOEK_JOBB
- MOETE_MED_NAV (Varsel når det er 24h igjen til møte)
- SAMTALEREFERAT
- JOBB_JEG_VIL_SOEKE
- JOBB_JEG_HAR
- JOBBRETTET_EGENAKTIVITET
- MEDISINSK_BEHANDLING

AktivitetStatus:
- FORSLAG
- PLANLEGGER
- GJENNOMFOERER
- FULLFOERT
- AVBRUTT

- Events:
  - CREATE
  - CREATED
  - MODIFY
  - MODIFIED
  - ERROR
  - ARCHIVED

```json
{
  "transactionId": "45fa0953-c906-405c-9dc6-81196d66ce5a",
  "timestamp": "",
  "type": "AKTIVITET",
  "event": "MODIFIED",
  "oppfolgingsPeriodeId": "ec7b3d53-1041-4ded-8442-4fa7ffaccbad",
  "subject": {
    "type": "BRUKER",
    "norskIdent": "01011074635",
    "aktoerId": "fd"
  },
  "actor": {
    "type": "BRUKER",
    //VEILEDER, BRUKER, SYSTEM
    "id": "01011074635",
    "aktoerId": "fd"
  },
  "data": {
    "aktivitetId": "ae0f07f4-959b-4497-b002-6a3cf6f615f7",
    "type": "MOETE_MED_NAV",
    "oldState": {
      "title": "",
      "description": "",
      "avtalt": false,
      "forhaandsorientering": {
        "type": "SEND", //IKKE_SEND
        "text": "",
        "lestTidspunkt": ""
      },
      "status": "PLANLEGGER",
      "fraTidspunkt": "",
      "tilTidspunkt": "",
      "kontorsperretEnhet": "",
      "opprettetDate": "",
      "endretDate": "",
      "archivedDate": "",
      "subtypeData": {
        "tidspunkt": "01.01.2021 00:00",
        "motested": "",
        "moteform": ""
      }
    },
    "newState": {
      "title": "",
      "avtalt": true,
      "status": "GJENNOMFOERER",
      "tidspunkt": "02.01.2021 02:00"
    }
  }
}
```

### Veilarbdialog

---

### Messages
#### Consumes
- sendVarsel
  ```json
  {
    "id": "6add527f-cb94-4033-889c-98497c216b06",
    "type": "SEND_VARSEL",
    "subType": "GENERIC",
    "timestamp": "",
    "version": 1,
    "payload": {
      "aktoerId": "89300080101",
      "fnr": "10108000398",
      "type": "OPPGAVE", //OPPGAVE, MELDING
      "message": "Dette er en generell varsel",
      "link": "http://nav.no"
    }
  }
  ```
- varselOppgaveComplete
  ```json
  {
    "id": "6add527f-cb94-4033-889c-98497c216b06",
    "type": "VARSEL_OPPGAVE_COMPLETE",
    "timestamp": ""
  }
  ```
#### Produces
- varselSent
  ```json
  {
    "id": "6add527f-cb94-4033-889c-98497c216b06",
    "type": "VARSEL_SENT",
    "timestamp": ""
  }
  ```
- varselFailed
  ```json
  {
    "id": "6add527f-cb94-4033-889c-98497c216b06",
    "type": "VARSEL_FAILED",
    "timestamp": "",
    "payload": {
      "errorCode": 4937,
      "errorMessage": "Fødselsnummer eksisterer ikke"
    }
  }
  ```
  
### Error Handling
#### Malformed message
- Send en varselFailed melding med feilmelding og feilkode
- Log feilmelding i loggen
- Send to grafana