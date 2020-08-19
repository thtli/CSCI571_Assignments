import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-picture-modal',
  templateUrl: './picture-modal.component.html',
  styleUrls: ['./picture-modal.component.css']
})
export class PictureModalComponent implements OnInit {

  @Input() pictureURL;
  openModal;
  constructor() { }

  ngOnInit() {
  }

  getImages() {
    this.openModal = true;
  }
  closeModal() {
    this.openModal = false;
  }
}
