# SnapHunt

SnapHunt è un'applicazione di intrattenimento basata su sfide fotografiche, realizzata come progetto finale per il corso di **Programmazione di Sistemi Mobile** (A.A. 2025/26) presso l'Università di Bologna.

## Obiettivo
L'app propone sfide fotografiche dinamiche: l'utente deve catturare oggetti specifici estratti casualmente dal sistema. Un'intelligenza artificiale integrata analizza le foto in tempo reale per verificare che il soggetto corrisponda alla richiesta, validando il completamento della sfida.

## Caratteristiche
- **Riconoscimento Immagine:** Analisi tramite modelli TensorFlow Lite e MediaPipe Tasks Vision, ottimizzati per performance e leggerezza direttamente sul dispositivo.
- **Gamification:** Sistema di punti, badge e statistiche personali per tracciare i progressi e premiare l'utente.
- **Persistenza e Cloud:** Sincronizzazione multi-dispositivo tramite Supabase, garantendo la continuità dei dati, dello storico e dei badge.
- **Gestione Offline:** Operatività garantita anche in assenza di rete grazie all'archiviazione locale con la libreria Room, con successiva sincronizzazione automatica al ripristino della connessione.
- **Autenticazione:** Integrazione sicura tramite Google Credential Manager.

## Stack tecnologico
- **Linguaggio:** Kotlin
- **Framework:** Android SDK
- **Machine Learning:** TensorFlow Lite, MediaPipe Tasks Vision
- **Backend/Database:** Supabase 
- **Persistenza Locale:** Room
