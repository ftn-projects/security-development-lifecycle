import { Component } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { ApiService, KeyMetadata } from '../api.service';

@Component({
  selector: 'app-manage-keys',
  templateUrl: './manage-keys.component.html',
  styleUrl: './manage-keys.component.scss'
})
export class ManageKeysComponent {
  keyTypes: string[] = ['Symmetric', 'Asymmetric', 'HMAC'];
  displayedColumns: string[] = ['id', 'alias', 'currentVersion', 'keyType', 'allowedOperations', 'createdAt', 'rotatedAt', 'actions'];
  dataSource = new MatTableDataSource<KeyMetadata>();

  // Form values
  alias: string = '';
  selectedKeyType: string = '';
  
  // Loading states
  isLoading = false;
  isCreating = false;

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.loadKeys();
  }

  loadKeys() {
    this.isLoading = true;
    this.apiService.getKeys().subscribe({
      next: keys => {
        console.log(keys);
        this.dataSource.data = keys;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  isNoData = (index: number, item: any) => {
    return this.dataSource.data.length === 0;
  };

  addKey() {
    if (!this.alias || !this.selectedKeyType) {
      console.log('Please fill in all fields');
      return;
    }

    this.isCreating = true;
    this.apiService.createKey(this.alias, this.selectedKeyType).subscribe({
      next: key => {
        this.isCreating = false;
        console.log('Key created', key);
        this.loadKeys();
      },
      error: () => {
        this.isCreating = false;
      }
    });
  }

  deleteKey(id: string) {
    console.log('Deleting key', id);
    this.isLoading = true;
    this.apiService.deleteKey(id).subscribe({
      next: () => {
        console.log('Key deleted', id);
        this.loadKeys();
      },
      error: (error) => {
        console.error('Error deleting key', error);
      }
    });
  }

  rotateKey(id: string) {
    console.log('Rotating key', id);
    this.isLoading = true;
    this.apiService.rotateKey(id).subscribe({
      next: (response) => {
        console.log('Key rotated', response);
        this.loadKeys();
      },
      error: (error) => {
        console.error('Error rotating key', error);
      }
    });
  }
}
