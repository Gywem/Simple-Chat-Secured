# Simple-Chat-Secured
This chat has a security layer when sending messages.

There is a protocol established in order to ensure the integrity, authenticity and non-repudiation of the data transmitted. The protocol is based on SSL approach.

 * 	There are two nodes in the communication, A and B. Besides a certification authority also exists. A and B knows the public key (signature) from the certification authority as a premise. 

 * 	For cyphering the chat messages a symmetric key approach is used since is faster than the asymmetric. But for sharing the symmetric key a asymmetric approached is selected to ensure the secured transmission of such sensitive data. Nodes also need their own digital signature signed (certificate) by the certification authority.

The process is explained as follow:
 
 * 	Before A communicates to B or/and B to A, the sender request their own certificate to the certification authroity.

 * 	The certification authority (CA) give back the certificates and the nodes A and B check them with the public key they have from CA if it has been sent securely. When they have their own certificates, A and B are able to send  their own certificates each other. 

 * 	The symmetric key is then able to be transmitted along with the signature/certificate from the sender. It will be also checked.

 * 	When both nodes have the symmetric key for the communication they are able to send application data (chat messages) in a secure manner.

This project was meant for studying network security area and pattern designs for OO programming. Concretly, this project uses the **Singleton**, **Observer**, **MVC patterns**, **Command**, **Facade** and other patterns. This project also implements from scratch the **Triple DES** (symmetric) and **ARC4** (asymmetric) cipher methods.

## Technologies
Java

Cipher library for Java


## Bibliography
Head First Design Patterns - O'Reilly Media

Handbook of applied cryptography - Menezes, van Oorschot, Vanstone
