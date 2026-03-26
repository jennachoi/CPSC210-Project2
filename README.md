# Mood Tracker 
### A Simple Tool to Record and Reflect on Your Emotions

**What will the application do?**  
The Mood Tracker is a desktop Java application that designed to capture emotions more directly and make them easier to reflect on. Users can quickly record their daily mood with a simple score and an optional  note. The application also provides a monthly calendar view and visualizations that make it easy to spot patterns and fluctuations in mood at a glance. By focusing on emotional states rather than lengthy journaling, it helps users check in with themselves more immediately and consistently.  

**Who will use it?**  
Anyone who wants to track their emotions and reflect on their mental state, from students to professionals interested in self-reflection and growth.

**Why is this project of interest to you?**  
I’m interested in tools that support self-reflection and emotional well-being. Building an application that tracks mood patterns over time makes it easier to notice changes, reflect on habits, and manage overall mental health. 

## User Stories
- As a user, I want to add a **MoodEntry** with a mood score and optional note to my **MoodLog**.  
- As a user, I want to browse all the individual entries in my **MoodLog** so that I can read the moods and notes I recorded on specific days.
- As a user, I want to edit a **MoodEntry** if I need to correct or update the mood score or note.  
- As a user, I want to delete a **MoodEntry** if I no longer wish to keep it.  
- As a user, I want to see monthly statistics and a visualization of my **MoodLog** so that I can observe overall patterns and fluctuations in my moods.
- As a user, I want to save the entire state of my Mood Tracker (my MoodLog and its entries) to a JSON file when I choose to.
- As a user, I want to load my Mood Tracker state from a JSON file when I choose to, so I can resume exactly where I left off.

## Instructions for End User
- You can view the panel that displays the MoodEntries that have already been added to the MoodLog by selecting the Manage tab in the top navigation bar. This tab lists every saved MoodEntry along with its date, mood level, and optional note.
- You can generate the first required action related to the user story “adding multiple Xs to a Y” by clicking any of the mood buttons (e.g., 😊 Happy, 😐 Neutral, 😭 Very Sad) on the Home tab and optionally entering a note in the popup dialog. This action adds a new MoodEntry to your MoodLog.
- You can generate the second required action related to the user story “adding multiple Xs to a Y” by selecting an existing MoodEntry in the Manage tab and using either the Edit Entry button to modify its date, mood level, or note, or the Delete Entry button to remove it from your MoodLog.
- You can locate my visual component by visiting the Summary tab, where a monthly mood calendar and a mood distribution chart are displayed. These visualizations summarize your MoodLog and highlight overall mood trends.
- You can save the state of my application by clicking the Save Data button in the Save/Load tab. This writes your current MoodLog to a JSON file.
- You can reload the state of my application by clicking the Load Data button in the Save/Load tab to restore your previously saved MoodLog from the JSON file.

## Phase 4: Task 2
Below is a representative sample of the event log printed to the console when the program runs:

```text
=== Event Log ===
Wed Nov 26 13:17:16 PST 2025
Added Entry: 2025-11-26 | Neutral | Soso
Wed Nov 26 13:17:25 PST 2025
Updated Entry: 2025-11-25 | Neutral | Soso
Wed Nov 26 13:17:31 PST 2025
Added Entry: 2025-11-26 | Very Sad | Haaaaa
Wed Nov 26 13:18:21 PST 2025
Deleted Entry: 2025-11-26 | Very Sad | Haaaaa
```


## Phase 4: Task 3
### Design Reflection
If I had more time to work on the project, I would focus on refactoring to reduce duplication and separate responsibilities more cleanly. In the UI package, several tabs share similar layout and component-building patterns (for example, repeated BorderLayout setup, label styling, and color/icon lookup). These patterns could be extracted into shared helper methods or a small UI utility class to improve cohesion and reduce duplication, making the codebase easier to maintain and less error-prone. In addition, the `MoodTrackerGUI` class currently acts both as the main application controller and as a provider of visual resources (such as icons and colors). Introducing a dedicated `ThemeManager` or `MoodResources` class to encapsulate these presentation details would better respect the single-responsibility principle and make the GUI more modular.

I would also consider refactoring how persistence is handled. At the moment, the `ManageTab` directly constructs and uses `JsonReader` and `JsonWriter`, which couples the UI layer to the persistence mechanism. A cleaner design would introduce a dedicated persistence service or controller that is responsible for saving and loading `MoodLog` objects, while the UI only invokes high-level operations on that service. This would decouple the model and UI from the persistence implementation and make it easier to test and swap out the storage strategy in the future. Overall, the current design is functional and meets the project requirements, but these refactorings would improve maintainability, modularity, and readability if additional development time were available.

