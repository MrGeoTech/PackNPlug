# Protocol
## Definitions:
-   ID - 1 byte
-   VarString
  -   4 byte (length)
  -   X bytes (String)

## Server Bound:
-   ### Get Download Url
  -   ID: 0x00
  -   VarString: name[:version]


-   ### Get Info
-   ID: 0x02
-   VarString: name[:version]


-   ### Get Funding
-   ID: 0x04
-   VarString: name[:version]

## Client Bound:
-   ### Get Download Url (Return)
-   ID: 0x01
-   VarString: url OR "null"


-   ### Get Info (Return)
-   ID: 0x03
-   VarString: [version;url;supportedVersions;authors;fundingUrl] OR "null"


-   ### Get Funding (Return)
-   ID: 0x05
-   VarString: fundingUrl OR "null"