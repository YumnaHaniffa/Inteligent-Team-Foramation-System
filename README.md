# 🎮 TeamMate – Intelligent Team Formation System

**Automated balanced team builder for University Gaming Clubs** 

![Java](https://img.shields.io/badge/Java-17+-007396?logo=openjdk&logoColor=white)
![OOP](https://img.shields.io/badge/Paradigm-Object_Oriented-blue)
![Git](https://img.shields.io/badge/Version_Control-Git-black?logo=git)

---
## 📋 Project Overview

Tired of manually forming unbalanced gaming teams?  

**TeamMate** is a **fully object-oriented Java desktop application** that automatically creates diverse, well-balanced teams for tournaments, friendly matches, or inter-university events.

It collects participant data through a structured survey, classifies personality types, and uses a smart matching algorithm to ensure every team has:
- Mixed game interests
- Role variety
- Balanced personality types
- Fair skill distribution

Built from scratch using **pure OOP principles**, UML-driven design, file handling, exception management, and multi-threading.

---
## ✨ Key Features

- **Interactive Survey System** – Participants fill in skills, preferred roles & 5 personality questions
- **Personality Classifier** – Automatically assigns: **Leader (90-100)** | **Balanced (70-89)** | **Thinker (50-69)**
- **Smart Team Builder** – Forms teams of any size with:
  - Game diversity
  - Role variety
  - Personality balance
  - Skill fairness
- **CSV File Handling** – Load participants + Save formed teams
- **Robust Exception Handling** – Validates all inputs & file operations
- **Concurrency** – Parallel processing of survey data and team formation (using Threads)
- **Console-based UI** with clear menus and error messages

---
## 🛠️ Technologies & OOP Concepts Demonstrated

| Concept                  | Implementation |
|-------------------------|--------------|
| **Encapsulation**       | Private fields + Getters/Setters in `Participant`, `Team` |
| **Polymorphism**        | Overridden methods in classifiers & builders |
| **Abstraction**         | Abstract classes & interfaces for services |
| **File I/O**            | `FileService` for CSV read/write |
| **Exception Handling**  | Custom exceptions + try-catch everywhere |
| **Concurrency**         | `Thread` & `Runnable` for parallel team formation |
| **UML Design**          | Full Use Case, Activity, Class & Sequence diagrams |


---
## 🚀 How to Run

### Option 1: Using IDE (Recommended)
1. Clone the repository:
   ```bash
   git clone https://github.com/yumna-prog/TeamMate.git

