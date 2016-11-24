# DigitalSignature #Project forked from sew62.
uses the RSA encryption algorithm to sign a file to test if it was altered

Program creates a public and private key through the RSA algorithm, then opens a file to sign.
The signature is calculated from a SHA-256 hash of the original files contents, then the key is used to obtain the signature.
The signature is placed on the first line of the newly creaded file with the original document attached underneath it.

If the file has not been altered, the first line decrpyted with the private key should be identical to the remaining message
hashed with the SHA-256 algorithm. An output statement will tell the user whether the file has been altered or not.

Command Line Arguments:

To place your digital signature in a file: args[1]: s args[2]: filetosign
To verify that file was not altered: args[1]: v args[2]: filetoverify
