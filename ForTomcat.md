Tomcat version of Wiimote over HTTP:

# HOW TO #

  1. Start Tomcat service
  1. Install WiimoteOverHTTP.war file into webapps folder
  1. http://127.0.0.1:8080/WiimoteOverHTTP/execute?method=findWiimote to start detecting Wiimote
  1. Push 1 and 2 button both on your wiimote
  1. LED would indicate the number of Wiimote (max: 7 wiimotes!)
  1. Enjoy!
  1. Call up the "releaseWiimote" method when finishing

# REQUIREMENTS #

  * J2SE 5.0 or later
  * Tomcat 5.5 or later
  * BlueCove
  * WIDCOMM Driver
and
  * Wiimote!

# TESTED BLUETOOTH ADAPTERS #

  * USB-DO-BT/CL