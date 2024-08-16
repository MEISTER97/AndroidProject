# Gaming Social App

## Project Overview
The project is a gaming social app where players can register and search for other players with common interests.

## App Structure

### Splash Activity
- The app starts with a splash activity that transitions transparently to the login activity.

### Registration and Login
- Users can register or log in using email or phone.

### First-Time User Setup
- First-time users must set up their accounts.
- The account name cannot be empty.
- Users must select at least one interest and platform; otherwise, their profiles will not be searchable.

### Profile Picture Selection
- Users can select from up to 9 profile pictures (8 predefined and 1 default).

### Search Activity
- Allows users to search for others in Firestore.
- If the name input is empty, it will search all users.
- At least one GameGenre and platform must be selected.
- Utilizes fragments: one for search input and one for a RecyclerView to display search results.

### Profile and Friend Requests
- Users can visit profiles and send "add friend" requests.
- Cannot add a friend if already friends or if a friend request has already been sent.
- Friend requests appear in the target user's notification activity.

### Notification Activity
- Users can accept or decline friend requests received.

### Chat Activity (GoodGameChat)
- Users can start conversations with their friends.
- Displays up to the last 30 messages.
- Green messages represent those sent by the user; orange messages represent those sent by friends.

### Community Activity
- Any user can create a group (group names must be unique).
- Users can join groups and write messages.
- Green messages represent those sent by the user; orange messages represent those sent by other users.
