## Attack surface

| Surface            | Endpoint                          | Input                                | AuthNZ        | Data                                 |
| ------------------ | --------------------------------- | ------------------------------------ | ------------- | ------------------------------------ |
| Auth               | POST `/auth`                      | username, password                   | public        | users table, jwt token               |
| Key create         | POST `/keys/create`               | alias, keyType                       | **manager**   | key metadata, wrapped material       |
| Key rotate         | POST `/keys/rotate`               | keyId                                | **manager**   | new wrapped material, version update |
| Key delete         | DELETE `/keys/{id}`               | keyId                                | **manager**   | key metadata, all key materials      |
| Key list           | GET `/keys`                       | /                                    | authenticated | key metadata                         |
| Key get            | GET `/keys/{id}`                  | keyId                                | authenticated | key metadata                         |
| Encrypt (AES-GCM)  | POST `/crypto/encrypt/symmetric`  | keyId, message, version?             | **user**      | unwrap key material                  |
| Decrypt (AES-GCM)  | POST `/crypto/decrypt/symmetric`  | keyId, ciphertext, version?          | **user**      | unwrap key material                  |
| Encrypt (RSA-OAEP) | POST `/crypto/encrypt/asymmetric` | keyId, message, version?             | **user**      | unwrap public key material           |
| Decrypt (RSA-OAEP) | POST `/crypto/decrypt/asymmetric` | keyId, ciphertext, version?          | **user**      | unwrap private key material          |
| HMAC               | POST `/crypto/*/hmac`             | keyId, message, signature?, version? | **user**      | unwrap key material                  |
| Sign (RSA-SHA256)  | POST `/signatures/*`              | keyId, message, signature?, version? | **user**      | unwrap key material                  |
| CORS               | all                               | Origin headers, preflight            | enforced      | /                                    |
| Log files          | /                                 | /                                    | admin/ops     | application logs                     |


The deployment environment requires network security with proper firewall rules and port restrictions. Server hardening includes OS patches and disabling unnecessary services. Database access controls and data encryption are critical. Secrets must be securely stored and managed. Monitoring and backup procedures need proper security controls.
