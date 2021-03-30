# VeilarbVarsel

## Todos:
- Sende melding på 3 forskjellige MQ
- Motta melding på Kafka
- bruke den gamle funksjonen
- Utforske topic `aapen-dok-notifikasjon-status` for notifikasjoner (https://navikt.github.io/brukernotifikasjon-docs/eksternvarsling/)
- Trenger vi lagre?
- Grafana


## Kafka

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