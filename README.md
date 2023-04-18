# EyeTunes
A visualization of music inspired by chromesthesia.

As the figure outlines, the main class, \texttt{CombineClasses}, reads MIDI input, isolates the musical events, and stores them in \texttt{Note} objects. \texttt{CombineClasses} also invokes \texttt{CreateDB} to create a database, \texttt{mappings}, of all of the audiovisual mappings that link the musical input to the visual output. \texttt{CombineClasses} then uses the \texttt{Note} objects and \texttt{mappings} data to 
identify the associated graphics to create for each musical event. 

\texttt{Shape} is an abstract data type (ADT) with methods inherited by the subclasses enumerated in Figure 4.1. \texttt{CombineClasses} creates \texttt{Shape} objects and calls their draw methods as it plays their corresponding sounds from the MIDI data.

<img width="1435" alt="architecture" src="https://user-images.githubusercontent.com/70870417/232639841-e86f98a8-557a-4515-953f-5c9f2e2a5088.png">
