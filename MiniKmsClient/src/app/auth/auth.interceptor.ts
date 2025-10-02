import { HttpInterceptorFn, HttpErrorResponse, HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, Observable, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const token = localStorage.getItem('token');
  
  let authReq = req;
  if (token) {
    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

    console.log('Outgoing request:', {
    url: authReq.url,
    method: authReq.method,
    headers: authReq.headers.keys().reduce((acc, key) => {
      acc[key] = authReq.headers.get(key);
      return acc;
    }, {} as any),
    body: authReq.body,
    params: authReq.params.toString()
  });

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 || error.status === 403) {
        // localStorage.removeItem('token');
        // router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};

@Injectable()
export class AuthInterceptorService implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return authInterceptor(req, next.handle.bind(next));
  }
}
