import { Component } from '@angular/core';
import { ApiService, CryptoDTO, EncryptType } from '../../api.service';

@Component({
  selector: 'app-crypto-ops',
  templateUrl: './crypto-ops.component.html',
  styleUrls: ['./crypto-ops.component.scss']
})
export class CryptoOpsComponent {
  message: string = '';
  keyId: string = '';
  version: number | null = null;
  hmacBase64: string | null = null;
  encryptType: EncryptType = 'SYMMETRIC';

  result: string = '';
  isLoading = false;

  constructor(private apiService: ApiService) {}

  private buildDTO(): CryptoDTO {
    return {
      message: this.message,
      keyId: this.keyId,
      version: this.version,
      hmacBase64: this.hmacBase64
    };
  }

  doEncrypt() {
    this.isLoading = true;
    this.apiService.encrypt(this.buildDTO(), this.encryptType).subscribe({
      next: res => { this.result = res; this.isLoading = false; },
      error: (err) => {this.isLoading = false; console.log(err);}
    });
  }

  doDecrypt() {
    this.isLoading = true;
    this.apiService.decrypt(this.buildDTO(), this.encryptType).subscribe({
      next: res => { this.result = res; this.isLoading = false; },
      error: (err) => {this.isLoading = false; console.log(err);}
    });
  }

  computeHmac() {
    this.isLoading = true;
    this.apiService.computeHmac(this.buildDTO()).subscribe({
      next: res => { this.result = res; this.isLoading = false; },
      error: () => this.isLoading = false
    });
  }

  verifyHmac() {
    this.isLoading = true;
    this.apiService.verifyHmac(this.buildDTO()).subscribe({
      next: res => { this.result = res ? "VALID" : "INVALID"; this.isLoading = false; },
      error: () => this.isLoading = false
    });
  }
}
