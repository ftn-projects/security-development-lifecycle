import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../environments/environment';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

export interface KeyMetadata {
  keyId: string;
  alias: string;
  primaryVersion: number;
  keyType: string;
  allowedOperations?: string[];
  createdAt?: Date;
  rotatedAt?: Date;
}

export interface CryptoDTO {
  message: string;
  keyId: string;
  version: number | null;
  hmacBase64: string | null;
}

export interface SignRequestDTO{
  message: string;
  version: number | null;
}

export interface VerifyRequestDTO{
  message: string;
  signature: string;
}

export type EncryptType = "SYMMETRIC" | "ASYMMETRIC";

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/auth`, credentials);
  }

  getKeys(): Observable<KeyMetadata[]> {
    return this.http.get<KeyMetadata[]>(`${this.baseUrl}/keys`);
  }

  createKey(alias: string, keyType: string): Observable<KeyMetadata> {
    return this.http.post<KeyMetadata>(`${this.baseUrl}/keys/create`, { alias, keyType });
  }

  rotateKey(id: string): Observable<KeyMetadata> {
    return this.http.post<KeyMetadata>(`${this.baseUrl}/keys/rotate`, { id });
  }

  deleteKey(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/keys/${id}`);
  }

  encrypt(cryptoDTO: CryptoDTO, encryptType: EncryptType): Observable<string> {
    const url = encryptType === 'SYMMETRIC' 
      ? `${this.baseUrl}/crypto/encrypt/symmetric`
      : `${this.baseUrl}/crypto/encrypt/asymmetric`;

    return this.http.post(url, cryptoDTO, { responseType: 'text' });
  }

  decrypt(cryptoDTO: CryptoDTO, encryptType: EncryptType): Observable<string> {
    const url = encryptType === 'SYMMETRIC' 
      ? `${this.baseUrl}/crypto/decrypt/symmetric`
      : `${this.baseUrl}/crypto/decrypt/asymmetric`;

    return this.http.post(url, cryptoDTO, { responseType: 'text' });
  }

  computeHmac(cryptoDTO: CryptoDTO): Observable<string> {
    return this.http.post(`${this.baseUrl}/crypto/compute/hmac`, cryptoDTO, { responseType: 'text' });
  }

  verifyHmac(cryptoDTO: CryptoDTO): Observable<boolean> {
    return this.http.post(`${this.baseUrl}/crypto/verify/hmac`, cryptoDTO, { responseType: 'text' })
      .pipe(
        map(res => res === 'true') // convert "true"/"false" string into boolean
      );
  }


  sign(keyId: string, signRequestDTO: SignRequestDTO): Observable<string> {
    return this.http.post(
      `${this.baseUrl}/signatures/sign`,
      signRequestDTO,
      {
        params: { keyId },
        responseType: 'text' // <-- treat response as plain text
      }
    );
  }

  verify(keyId: string, verifyRequestDTO: VerifyRequestDTO, version: number | null): Observable<boolean> {
    const params: any = { keyId };
    if (version !== null) {
      params.version = version.toString();
    }

    return this.http.post(
      `${this.baseUrl}/signatures/verify`,
      verifyRequestDTO,
      {
        params,
        responseType: 'text' // <-- treat response as plain text
      }
    ).pipe(
      map(res => res === 'true') // convert "true"/"false" string into boolean
    );
  }
}