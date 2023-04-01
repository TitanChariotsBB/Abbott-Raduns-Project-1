
# Dr. Zhang's Notes

The folder for the client is client_folder 
The PUBLIC_FOLDER in the server is the server_folder 

For edits, I want to try to not make changes to the master branch, but rather to a development branch.

# How to run

On the bottom left corner of IntelliJ, hit Terminal. Right-click on the terminal window, then hit split right.
On the server window type `java src\FtpServer.java`, and on the client window type `java src\FtpClient.java`.

# TODO

## Put more code in methods

Currently, all the file sending code is just sitting there,
not in a nice self-contained methods. One would need to pass
references to DataInputStream and DataOutputStream.

## Comment

Said methods need to be commented, 
and trickier sections need inline comments.