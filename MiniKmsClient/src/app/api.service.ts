import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
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
  version: number;
  hmacBase64: string | null;
}

export interface SignRequestDTO{
  message: string;
  version: number;
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

  encrypt(cryptoDTO: CryptoDTO, encryptType: EncryptType): Observable<String>{
    return(
    encryptType == 'SYMMETRIC' ?
      this.http.post<String>(`${this.baseUrl}/crypto/encrypt/symmetric`, cryptoDTO)
      :
      this.http.post<String>(`${this.baseUrl}/crypto/encrypt/asymmetric`, cryptoDTO)
    )
  }

  decrypt(cryptoDTO: CryptoDTO, encryptType: EncryptType): Observable<String>{
    return(
    encryptType == 'SYMMETRIC' ?
      this.http.post<String>(`${this.baseUrl}/crypto/decrypt/symmetric`, cryptoDTO)
      :
      this.http.post<String>(`${this.baseUrl}/crypto/decrypt/asymmetric`, cryptoDTO)
    )
  }

  computeHmac(cryptoDTO: CryptoDTO) : Observable<String>{
    return this.http.post<String>(`${this.baseUrl}/crypto/compute/hmac`, cryptoDTO)
  }

  verifyHmac(cryptoDTO: CryptoDTO) : Observable<String>{
    return this.http.post<String>(`${this.baseUrl}/crypto/verify/hmac`, cryptoDTO)
  }

  sign(keyId: number, signRequestDTO: SignRequestDTO): Observable<string> {
  return this.http.post<string>(
    `${this.baseUrl}/signature/sign`,
    signRequestDTO,
    { params: { keyId: keyId.toString() } }
  );
}

  verify(
    keyId: number,
    verifyRequestDTO: VerifyRequestDTO,
    version: number | null
  ): Observable<string> {
    let params: any = { keyId: keyId.toString() };
    if (version !== null) {
      params.version = version.toString();
    }

    return this.http.post<string>(
      `${this.baseUrl}/signature/verify`,
      verifyRequestDTO,
      { params }
    );
  }
}
