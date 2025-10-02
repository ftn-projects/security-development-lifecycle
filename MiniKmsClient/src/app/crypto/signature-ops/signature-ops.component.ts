import { Component } from '@angular/core';
import { ApiService, SignRequestDTO, VerifyRequestDTO } from '../../api.service';

@Component({
  selector: 'app-signature-ops',
  templateUrl: './signature-ops.component.html',
  styleUrls: ['./signature-ops.component.scss']
})
export class SignatureOpsComponent {
  keyId: string = '';
  version: number | null = null;

  message: string = '';
  signatureBase64: string = '';

  result: string = '';
  isLoading = false;

  constructor(private apiService: ApiService) {}

  doSign() {
    console.log('Token:', localStorage.getItem('token'));
    this.isLoading = true;
    const dto: SignRequestDTO = { message: this.message, version: this.version };

    this.apiService.sign(this.keyId, dto).subscribe({
      next: res => {
        console.log("YEEEEY");
        this.result = res;
        this.signatureBase64 = res; // auto-fill so user can verify
        this.isLoading = false;
      },
      error: (err) => {this.isLoading = false; console.log("Noooo"); console.log(err);}
    });
  }

  doVerify() {
    this.isLoading = true;
    const dto: VerifyRequestDTO = {
      message: this.message,
      signature: this.signatureBase64
    };

    this.apiService.verify(this.keyId, dto, this.version).subscribe({
      next: res => {
        this.result = res ? 'VALID' : 'INVALID';
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }
}
