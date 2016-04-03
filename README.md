## Inspiration

**Emergency Medical Response** is **not something to take lightly**; a few minutes in delayed response time, or a few omitted pieces of crucial information, can **tip the balance of life and death** to its more tragic end.
Our service, **EIR**, is designed to **augment the traditional 911 emergency response model**, by
- Better informing medical personnel with more detailed descriptors, via **Video**, **Graphics**, and **Geolocation**, about exactly where, and what, occurred in an accident.
- Providing medical personnel with an easier, more panic-proof way of giving instructions to those at the scene, via our diagrammatic Canvas and a simple text interface.
**EIR** helps those in grave medical emergency.  We intuitively aide the **Essential Communication** between **Caller** and **Responder** by opening a line of communication among the person reporting the incident, the emergency dispatcher, and the paramedics via **Graphical**, **Visual**, **Textual** and **Auditory** means.

## What it does
**EIR** offers a new way for people in emergency situations to **better communicate** with **Medical Personnel**.
- Our **Canvas** feature allows emergency responders to **illustrate** critical first aid techniques **quickly** and **clearly**, allowing panicked individuals to see exactly what they need to do, when, and how. 
- Our **Messaging** feature allows instructions to be seen in a textual way on the smartphone screen, doing away with the imprecision of memory ubiquitous in times of crisis.
- Our **Video** feature allows **First Responders** to see firsthand the accident in question; the clarity of this picture can be worth a thousand words.

In order to make the process of illustrating complex tasks possible in a very short amount of time, we planned on adding a feature that automatically recommends relevant illustration setups to the emergency responders, so that they can find the right information for the right task in the shortest time possible with minimum manual searches. We were going to implement this feature using IBM's Speech-to-Text API, popularized by the **Watson** machine. This API allows us to predefine a set of indicator keywords that allows the algorithm to quickly classify relevant information.

Our implementation of this last feature was hampered by the difficulty of cloning the **AudioStream** component of a **VideoStream** in Android in real time. Given more time, we will implement this feature, and use it to ensure that **Medical Responders** are reminded of relevant queries they need to know; from this, improved patient outcomes will inevitably result.

## How we built it

The application itself consists of an **Android App**, for someone at the scene of an accident to call for help, and a **Web Application**, made in **HTML/CSS/JS**, which is designed for a medical responder to communicate with the caller more effectively than is possible through traditional, voice-only communication in a 911 call.

These two applications communicate through **HTTP GET** requests, via a **Django Server** on the **Linode** hosting service. Our audio and video calling is implemented through the **Sinch API**, and the **Canvas** drawing system is implemented through our implementation of **HTML5's Canvas Element** alongside the **Literally Canvas** plugin.

## Challenges we ran into
We ran into some struggles implementing our server on **Linode**, because we had some issues installing the software when it came to that. Additionally, we had troubles implementing our foray into the **IBM's Watson API**, mostly streaming from the fact that we would have been required to separate an audio stream from a video stream on the fly in real time on the Android device.

## Accomplishments that we're proud of

We are proud of the fact that we were able to implement two features inexplicably absent from emergency response today: **Graphical Advice Giving** (implemented through the Canvas) and **Video Streaming from the Scene** (implemented through Sinch). We feel that these two additions will have a tangibly beneficial effect on patient outcomes in regards to emergency response.

**Socially conscious hacking** is important to us; too often is innovation ignored, at least when it comes to projects with limited revenue potential at first glance. We believe that the tried-and-true Silicon Valley way of tackle old problems with distruptive software solutions should be applied to the public sector for the public good, and we are proud to have followed our beliefs up with action.

## What we learned
Before this Hackathon, we as a group had very little experience with the **Django** platform, let alone implementing a server on a remote Ubuntu host like **Linode**. This Hackathon allowed us to grow our Back-End skillsets, by getting our hands dirty implementing code directly upon a hosted machine with **root access**.

## What's next for EIR

- Fine tune **Speech-to-Text API** from **IBM**, fixing the **AudioStream** separation issue.
- Implement an **Unsupervised Machine Learning Algorithm** that recommends previously utilized illustrations given specific speech inputs, to speed up the work of the dispatcher and the paramedics
- More Extensive Research on the mechanisms of current EMS system; we will **Iteratively Design** our System to take this research and apply it in the field. 
