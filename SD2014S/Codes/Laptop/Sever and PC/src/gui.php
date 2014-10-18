<!DOCTYPE html>
<html>
    <head>
        <title>Login</title>
    </head>
    <body style="background-color: #E5E5E5;">
        <div style="text-align: center; padding-top: 25px; width: 350px; margin: 0 auto">
            <object type="application/x-java-applet" width="350" height="450">
                <param name="code" value="GUI.class">
                <param name="server_ip" value=<?php echo $_POST["server_ip"]; ?>/>
                <param name="client_ip" value=<?php echo $_POST["client_ip"]; ?>/>
                Please install the latest version of Java to view this applet.
            </object>
        </div>
    </body>
</html>
