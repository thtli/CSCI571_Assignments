import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-photos-tab',
  templateUrl: './photos-tab.component.html',
  styleUrls: ['./photos-tab.component.css']
})
export class PhotosTabComponent implements OnInit {
  @Input() selectedImages; 

  constructor() { }

  ngOnInit() {

  }
}

