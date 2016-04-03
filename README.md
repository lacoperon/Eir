# EIR - Emergency Integrated Response

Emergency Medical Response is not something to take lightly; a few minutes in delayed response time, or a few omitted pieces of crucial information, can tip the balance between life and death to its more tragic end.

Our service, **EIR**, is designed to augment the traditional _911_ emergency response service, by 
 - better informing medical personnel with more detailed descriptors, via video  and geolocation, about exactly where, and what, occurred in an accident.
 - providing medical personnel with an easier, more panic-proof way of giving instructions to those at the scene, via our diagrammatic Canvas and a simple text interface.

The application itself consists of an **Android App**, for someone at the scene of an accident to call for help, and a **Web Application**, which is designed for a medical responder to communicate with the caller more effectively than is possible through traditional, voice-only communication in a _911_ call.

These two applications communicate through **HTTP GET** requests, communicated to a **Django Server** on the Linode hosting service. Our audio and video calling is implemented through the **Sinch** API, and the Canvas drawing system is implemented through an implementation of **HTML5's Canvas Element**.

Created as part of **HackPrinceton Spring 2016**.
