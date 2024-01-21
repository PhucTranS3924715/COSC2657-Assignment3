Group: ECLAIR_0_1
IMPORTANT: Note for the app:
- When installing the app to the phone, there will be 3 app icons. The first icon is the app for Admin, the second is for Customer and the third is for Driver. To open the app for different users, terminate the app first, then click the other icon to open the app for other users.

Work contribution:
Tran Dai Phuc - S3924715:
- Create basic OOP for the app, including User, Customer, Driver, Admin, PaymentMethod and Voucher class.
- Implement basic UI and Java code for the customer main screen, which includes a view pager with 4 fragments (Home, Activity, Vouchers and Profile) and a bottom navigation. Can navigate between fragments by clicking on the corresponding icon in the bottom navigation bar.
- Implement UI and Java code for login and sign up screen for customers.
- Implement all features for Admin, include login screen, view users list and users details, create account for drivers, view and create vouchers.
- Implement message UI and Java code for customers and drivers.
- Implement choosing profile pictures from the phone’s gallery.
- Implement Java code for choosing payment method before booking a ride, implement UI and Java code to add a payment method to a customer account.
- Implement Java code for driver profile screen, implement UI and Java code for edit profile information for drivers.

Dinh Gia Bao – S3877923:
- Implement UI for driver profile.
- Implement UI for bad rating screen.
- Implement UI and Java code for customer home screen, including Places API, autocomplete fragment and UI change when select vehicles.
- Implement UI for customer activity.
- Modify profile fragment UI and Java code, which allow customer to enter SOS setup.
- Implement UI and Java code for SOS setup, which include storing customer sosPhone and sosMessage.
- Modify UI and Java code for BookingFragment, which is SOS button and SmsManager function to send SOS message base on sos's phone number and sos's message of customer from database.

Nguyen Hong Anh - S3924711:
- Implement UI for voucher screen.
- Implement Java code for MyFireBaseMessagingService, which include update driver FCM token.
- Implement UserMapActivity, which include search for available driver in specific radius, get current user location, implement Haversine formula to calculate distance, get ride document id after driver is found.
- Implement DriverMapActivity, which include update current location of driver periodically and upload it to Firestore, implement basic draw route method.
- Implement UI and Java code for Profile, which include update customer avatar periodically.
- Implement UI and Java code for EditProfile, which include update user new information to Firestore, update user's avatar to firebase storage after user choose an image from their gallery.
- Implement UI and Java code for BookingFragment, which include draw route from driver current location to pick up point, and route from driver location ( at pick up point) to drop off point, interaction for cancel button.
- Implement Java code Rating, BadRating, and GoodRating, which include transition depend on customer feedback.
- Modify OOP for the app, including Ride, Driver, User to suit with the application.
- Modify draw route and Autocomplete interaction in HomeFragment for desired functionality.

Vo Hoang Khanh – S3926310:
- Implement UI for payment method screen.
- Implement MyFireBaseMessagingService and NotificationService in handling notification.
- Implement Booking process in UserMapActivity.
- Implement resetting password for Firebase Authentication.
- Implement trip's price logic and route generating in HomeFragment.
- Implement DriverMapActivity, handling: + Accepting Ride from the Customer (RideDetailsActivity).
                                         + Navigation from the driver's current location to the pick up point.
                                         + Navigation from the pick up point to the destination.
                                         + Salary for the driver after the trip is completed.
                                         + Saving the ride detail in History.
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
- Drivers can login.
- Admin can sign in with a predefined account (Email: admin@gmail.com; Password: 123456).

Customers:
- Can view all available vouchers.
- Can edit their profile information, including change avatar, can delete their account.
- Can add payment methods and choose payment methods when booking a ride.
- Can view vouchers list.
- Can edit their profile information, including change avatar.
- Can set SOS information to include phone and message, or change already existing SOS information.

Drivers:
- Can view, edit and delete their account.
- Can chat with customer.
- Accept book order from customer.

Admin:
- Can view all users details, including customers and drivers.
- Can create a driver account.
- Can view and create more vouchers.
- Can sort customers and drivers list (sort by name or email).

Issues and bugs:
- Message feature display some messages with a big space between each message.
- Only tested message activity for customer app, have not test for driver app (may produces errors).
- Displaying route is RNG-based due to network issues (Or RMIT wifi is just built differnt).
- Place API is not usable multiple time a day. We added a default locations and route for demonstration.
- The SOS function while the customer is on route can click to send an SMS to the SOS phone number with the message. However, the display while on the trip blocks the SOS button so we can't test it.
