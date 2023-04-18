# EyeTunes
A visualization of music inspired by chromesthesia.

As the diagram outlines, the main class, CombineClasses, reads MIDI input, isolates the musical events, and stores them in Note objects. CombineClasses also invokes CreateDB to create a database, mappings, of all of the audiovisual mappings that link the musical input to the visual output. CombineClasses then uses the Note objects and mappings data to identify the associated graphics to create for each musical event. 

Shape is an abstract data type (ADT) with methods inherited by the subclasses enumerated in the figure. CombineClasses creates Shape objects and calls their draw methods as it plays their corresponding sounds from the MIDI data.

<img width="1435" alt="architecture" src="https://user-images.githubusercontent.com/70870417/232639841-e86f98a8-557a-4515-953f-5c9f2e2a5088.png">
