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
}
