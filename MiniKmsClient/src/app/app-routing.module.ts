import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard } from './auth/auth.guard';
import { ManageKeysComponent } from './manage-keys/manage-keys.component';
import { LoginComponent } from './login/login.component';
import { CryptoComponent } from './crypto/crypto.component';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { 
    path: 'manage-keys', 
    component: ManageKeysComponent,
    canActivate: [authGuard],
    data: { roles: ['manager'] }
  },
  { 
    path: 'crypto', 
    component: CryptoComponent,
    canActivate: [authGuard],
    data: { roles: ['manager', 'user'] }
  },
  { path: '**', redirectTo: '/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
