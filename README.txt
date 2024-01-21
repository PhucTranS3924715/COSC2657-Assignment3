Group: ECLAIR_0_1
IMPORTANT: Note for the app:
- When installing the app to the phone, there will be 3 app icons. The first icon is the app for Admin, the second is for Customer and the third is for Driver. To open the app for different users, terminate the app first, then click the other icon to open the app for other users.
Work contribution:
Phuc:
- Create basic OOP for the app, including User, Customer, Driver, Admin, PaymentMethod and Voucher class.
- Implement basic UI and Java code for the customer main screen, which includes a view pager with 4 fragments (Home, Activity, Vouchers and Profile) and a bottom navigation. Can navigate between fragments by clicking on the corresponding icon in the bottom navigation bar.
- Implement UI and Java code for login and sign up screen for customers.
- Implement all features for admin, include login screen, view users list and users details, create account for drivers, view and create vouchers.
- Implement message UI and Java code for customers and drivers.
- Implement choosing profile pictures from the phone’s gallery.
Bao:
- Implement UI for driver profile. 
- Implement UI and Java code for customer home screen, including Places API and autocomplete fragment.
- Implement UI for bad rating screen.
Hong Anh:
- Implement UI for voucher screen.
- Implement Java code for MyFireBaseMessagingService, which include update driver FCM token.
- Implement UserMapActivity, which include search for available driver in specific radius, get current user location, implement Haversine formula to calculate distance, get ride document id after driver is found.
- Implement DriverMapActivity, which include update current location of driver periodically and upload it to Firestore, implement basic draw route method.
- Implement UI and Java code for Profile, which include update customer avatar periodically.
- Implement UI and Java code for EditProfile, which include update user new information to firestore, update user's avatar to firebase storage after user choose an image from their gallery.
- Implement UI and Java code for BookingFragment, which include draw route from driver current location to pick up point, and route from driver location ( at pick up point) to drop off point, interaction for cancel button.
- Implement Java code Rating, BadRating, and GoodRating, which include transition depend on customer feedback.
- Modify OOP for the app, including Ride, Driver, User to suit with the application.
- Modify draw route and Autocomplete interaction in HomeFragment for desired functionality.

Khanh:
- Implement UI for payment method screen.

Technologies used:
- Google Map View API: For customers and drivers to view the map. The customer can select the pickup point and destination, view the directions of the selected locations and view location of the driver. Driver can see the customer’s pickup location and view the direction.
- Places API: For customers to search for pickup locations and destinations.
- Direction API: Shows the direction between the pickup location and destination.
- Firebase Authentication: for user authentication and security, allows users to sign up or login.
- Firebase Firestore: stores and retrieves all users data, ensuring real time data sync.
- Firebase Messaging: allows the app to send notifications to drivers when a customer wants to book a trip and notifies users when a driver has accepted their request.
- Firebase Storage: allow the app to store user avatar.

Functionalities:
General functionalities:
- Customers can login or sign up.
- Admin can sign in with a predefined account and can create accounts for drivers.

Customers:
- Can view all available vouchers.
- Can edit their profile information, including change avatar.

Drivers:
- Can view their profile.

Admin:
- Can view all users details, including customers and drivers.
- Can create a driver account.
- Can view and create more vouchers.

Issues and bugs:
- Message feature display some messages with a big space between each message.
