import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { ManageKeysComponent } from './manage-keys/manage-keys.component';
import { MaterialModule } from './common/material/material.module';
import { AuthInterceptorService } from './auth/auth.interceptor';
import { CryptoOpsComponent } from './crypto/crypto-ops/crypto-ops.component';
import { SignatureOpsComponent } from './crypto/signature-ops/signature-ops.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    ManageKeysComponent,
    CryptoOpsComponent,
    SignatureOpsComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule,
    MaterialModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptorService,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
