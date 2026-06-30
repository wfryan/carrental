# Car Rental

This is a simulated car rental system designed with Object Oriented design principles in mind

Environment notes
---
- JUnit Release (maven tag - org.junit.jupiter:junit-jupiter:Release)
- I wrote this in Intellij on a Windows system.

What you can do!
---
- Create a Customer
- Rent a vehicle (Sedan, SUV, or Van)
- Add a vehicle into inventory
- Cancel a reservation
- Return a rental
- View the various cars that are often available
- View your upcoming reservations
---

What it does
---
- Loads and saves rental agreements/contracts to/from disk
- Loads and saves car inventory to/from disk
- Loads and saves customers to/from disk
- Vehicles can not have overlapping reservations, if all SUVs are in active rentals and off the lot, no SUV can be rented at the present moment
  - BUT since each rental has a duration, an SUV thats currently rented could be scheduled for the future, for after its current contract expiry
- Vehicle reservations get delayed if the previous reservation is still late.
