import { Component } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';

export interface KeyRecord {
  keyId: string;
  alias: string;
  keyType: string;
}

@Component({
  selector: 'app-manage-keys',
  templateUrl: './manage-keys.component.html',
  styleUrl: './manage-keys.component.scss'
})
export class ManageKeysComponent {
keyTypes: string[] = ['AES', 'RSA', 'HMAC'];

  displayedColumns: string[] = ['keyId', 'alias', 'keyType', 'actions'];
  dataSource = new MatTableDataSource<KeyRecord>([
    { keyId: '1', alias: 'Key One', keyType: 'AES' },
    { keyId: '2', alias: 'Key Two', keyType: 'RSA' },
    { keyId: '3', alias: 'Key Three', keyType: 'AES' },
    { keyId: '4', alias: 'Key Four', keyType: 'HMAC' }
  ]);

  addKey() {
    console.log('Add key clicked');
    // TODO: implement
  }

  deleteKey(key: KeyRecord) {
    console.log('Delete key', key);
    // TODO: implement
  }

  rotateKey(key: KeyRecord) {
    console.log('Rotate key', key);
    // TODO: implement
  }
}
