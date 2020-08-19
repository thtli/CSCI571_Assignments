<html>
  <head>
    <title>CS 571 HW 6</title>
    <meta charset="UTF-8">
    <style>
      h2 {
        text-align: center; 
        margin:0;
      }

      h4 {
        width: 700px;
        background-color: lightgray;
        color: black;
        text-align: center;
        margin: auto;
      }
      .box {
        border: 3px solid lightgray; 
        padding: 5px;
        background-color: whitesmoke;
        width: 500px;
        margin-left:auto;
        margin-right:auto;
      }

      #error_msg {
        text-align: center;
        border: 2px solid #d9d9d9; 
        background-color: #e6e6e6;
        width: 700px;
        margin-left:auto;
        margin-right:auto;
        display: none;
      }

      form {
        margin: 5px 15px; 
      }
      input, select {
        margin-top: 5px;
        margin-bottom: 5px;
        font-size: 70%;
      }
      
      .zipcodeBlock {
        display: inline-block;
        vertical-align: top;
      }
      
      .buttonBlock {
        text-align: center;
      }
      
      input:disabled+label{
      color: gray;
      }

      table, th, td {
        border: 1px solid lightgray;
        border-collapse: collapse;
        margin-left: auto;
        margin-right: auto;
      }

      .imagelinks {
        color: gray;
        text-align: center;
        margin-left: auto;
        margin-right: auto;
      }

      .imgButton {
        width: 40px; 
        height: 20px;
      }


      iframe {
        width: 80%;
        height: 100%;
        border:none;
        overflow-x: hidden;
        overflow-y: hidden;
      }
    </style>
    
    <script type="text/javascript">
      //Get user location from ip-api API
      function loadLocationJSON() {        
        var xmlhttp = new XMLHttpRequest();
       
        xmlhttp.open("GET", "http://ip-api.com/json", false); 
        xmlhttp.send();
        locationObj = JSON.parse(xmlhttp.responseText);
        document.getElementById("submitButton").disabled = false;

        root = locationObj.DocumentElement;
        userZip = locationObj.zip;
     
      }

      // Enable/Disable Form element Functions
      function zipFunction() {
        var x = document.getElementById("fromZipcode").checked;
        var y = document.getElementById("checkSearch").checked;

        // from Zipcode radio button or Enable Nearby Search aren't checked
        if (x == false || y == false) {
          document.getElementById("zipcode").disabled = true;
          if (x == false) {
            // if from zipcode option not checked, then zip should be user zip and any input in zipcode textbox is erased
            document.getElementById("fromHere").value = userZip;
            document.getElementById("zipcode").value = "";
          }
        }
        else {
          document.getElementById("zipcode").disabled = false;
        }
      }

      function searchFunction() {
        var x = document.getElementById("checkSearch").checked;
        if (x == false) {
          // enable nearby search is not checked, disable components
          opposite = true;
          // reset to default for the miles from here/zipcode options
          document.getElementById("milesFrom").value = "";
          document.getElementById("fromHere").checked = true;
          document.getElementById("zipcode").value = "";

        }
        else {
          opposite = false;
        }
        document.getElementById("milesFrom").disabled = opposite;
        document.getElementById("fromHere").disabled = opposite;
        document.getElementById("fromZipcode").disabled = opposite;
        zipFunction();
      } 

      function updateError(message) {
        document.getElementById("error_msg").style.display = "block";
        document.getElementById("error_msg").innerHTML = "<span>" + message + "</span>"
      }

      //Reset form & page
      function resetFunction(){
        //Clear results tables
        document.getElementById("error_msg").style.display = "none";
        document.getElementById("results").innerHTML = "";

        //return form to default values
        var formElems = document.getElementById("myForm").elements;

        for (var i=0; i < formElems.length; i++) {
          var current = formElems[i];

          if (current.type == "text") {
            current.value = "";
            if (current.id == "milesFrom") {
              current.disabled = true;
            }

          }
          else if (current.type == "select-one") {
            current.selectedIndex = 0; 
          }
          else if (current.type == "checkbox") {
            current.checked = false;
          }
          else if (current.type == "radio") {
            if (current.id == "fromHere"){
              current.checked = true;
            }
            current.disabled = true;
          }
        }
        //Reload location API for userZip
        loadLocationJSON();
        zipFunction();
      }

      //submit form - for seeing item details (clicking on item) 
      function itemFormSubmit() {
        document.getElementById("myForm").submit();
        
      }

      // Creating search results table
      function searchResults(js_data) {
          var items_arr = js_data.findItemsAdvancedResponse[0].searchResult[0].item;

            if (items_arr == undefined) {
              var message = "No Records has been found";
              updateError(message); 
            }

            else {
              
              var html_text = "<table style='width: 1200px;'><tbody><tr>";
              
              // Add headers for table
              var column_headers = ["Index", "Photo", "Name", "Price", "Zip code", "Condition", "Shipping Option"];

              for (var i = 0; i < column_headers.length; i++) {
                // Give Name and Shipping Option columns more space
                if (column_headers[i] == "Name") {
                  html_text+="<th style='width:55%'>";
                }
                else if (column_headers[i] == "Photo") {
                  html_text+="<th style='width:10%'>";
                }
                else if (column_headers[i] == "Shipping Option") {
                  html_text+="<th style='width:10%'>";
                }
                else if (column_headers[i] == "Condition") {
                  html_text+="<th style='width:10%'>";
                }
                else {
                  html_text += "<th>";
                }
                html_text += column_headers[i] + "</th>";
              }
              html_text += "</tr>"

              // Add table contents
              for (var i = 0; i < items_arr.length; i++) {
                var index = i + 1;
                //Column 1 (Index)
                html_text += "<tr><td>" + index + "</td>";
                
                //Column 2 (Photo)
                if (items_arr[i].galleryURL == undefined) {
                  html_text += "<td>N/A</td>";
                }
                else {
                  html_text += "<td><img src='"+ items_arr[i].galleryURL +"' width=100% height=100%></td>";
                }
                
                
                //Column 3 (Name)
                itemID = items_arr[i].itemId;
                html_text += "<td><button form='myForm' type='hidden' name='itemID' onclick='itemFormSubmit()' value='"+ itemID+"' style='font-family: inherit;font-size: inherit;border: none;background-color:inherit; cursor: pointer;'>"+ items_arr[i].title +"</button></td>";

                //Column 4 (Price)
                html_text += "<td>$"+ items_arr[i].sellingStatus[0].currentPrice[0].__value__ +"</td>";

                //Column 5 (Zip code)
                if (items_arr[i].postalCode == undefined) {
                  html_text += "<td>N/A</td>";
                }
                else {
                  html_text += "<td>"+ items_arr[i].postalCode +"</td>";
                }

                //Column 6 (Condition)
                var itemCondition;
                if (items_arr[i].condition == undefined) {
                  itemCondition = "N/A";
                }
                else {
                  itemCondition = items_arr[i].condition[0].conditionDisplayName;
                }
                html_text += "<td>"+ itemCondition +"</td>";

                //Column 7 (Shipping Option)
                var shippingCost;
                if (items_arr[i].shippingInfo[0].shippingServiceCost == undefined) {
                  shippingCost = "N/A";
                }
                else if (items_arr[i].shippingInfo[0].shippingServiceCost[0].__value__ == "0.0") {
                  shippingCost = "FreeShipping";
                }
                else {
                  shippingCost = "$" + items_arr[i].shippingInfo[0].shippingServiceCost[0].__value__;
                }
                html_text += "<td>"+ shippingCost +"</td></tr>";
              }

              html_text += "</tbody></table>";
              document.getElementById("results").innerHTML = html_text;
            }
      }

    </script>
    
  </head>
  
  <body onload="loadLocationJSON()">
    <?php
      $keyword = $category = $miles = $zipcode ="";
      $zipError = "";

      if($_SERVER["REQUEST_METHOD"] == "POST") {
        $keyword = urlencode(utf8_encode($_POST['keyword']));
        $category = $_POST['category'];

        // if Nearby Search miles is inputted
        if (isset($_POST['miles'])) {
          $miles = $_POST['miles'];
        }
        // if nearby miles not inputted but enable nearby search is selected
        // set miles to be 10 (default)
        if ($_POST['miles'] == "" && isset($_POST['enableSearch'])) {
          $miles = "10";
        }
        
        if (isset($_POST['zipcode'])){
          $zipcode = $_POST['zipcode'];
          if (!preg_match("/^\d{5}$/", $zipcode)) {
            $zipErr = "Zipcode is invalid";
            ?>
            <style type="text/css"> #error_msg {display: block;}</style>
            <?php
          }
        }
        else {
          $zipcode = $_POST['fromWhere'];
        }
      

        // If search button was pressed, no errors, then ebay search API call 
        if ($zipErr == "" && $_POST['submitButton'] == "Search") {
          // form URL for ebay Finding API call
          $url = "http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsAdvanced&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=TinaLi-ProductS-PRD-f16e2f5cf-50bf9e81&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD&paginationInput.entriesPerPage=20"; 

          //keyword
          $url .= "&keywords=$keyword";

          //category
          if ($category == "Art") {
            $url .= "&categoryId=550";
          }
          else if ($category == "Baby") {
            $url .= "&categoryId=2984";
          }
          else if ($category == "Books") {
            $url .= "&categoryId=267";
          }
          else if ($category == "Clothing") {
            $url .= "&categoryId=11450";
          }
          else if ($category == "Computers") {
            $url .= "&categoryId=58058";
          }
          else if ($category == "Health") {
            $url .= "&categoryId=26395";
          }
          else if ($category == "Music") {
            $url .= "&categoryId=11233";
          }
          else if ($category == "VideoGames") {
            $url .= "&categoryId=1249";
          }

          $filterNum = 0;
          $valueNum = 0;

          //zip code
          if ($zipcode != "") {
            $url .= "&buyerPostalCode=$zipcode";
            $url .= "&itemFilter($filterNum).name=MaxDistance&itemFilter($filterNum).value=$miles";
            $filterNum++;
          }

          //Shipping options
          if (isset($_POST['FreeShipping'])) {
            $url .= "&itemFilter(". $filterNum . ").name=FreeShippingOnly&itemFilter($filterNum).value=true";
            $filterNum++;
          }
          if (isset($_POST['LocalPickup'])) {
            $url .= "&itemFilter($filterNum).name=LocalPickupOnly&itemFilter($filterNum).value=true";
            $filterNum++;
          }

          //hide duplicate items
          $url .= "&itemFilter($filterNum).name=HideDuplicateItems&itemFilter($filterNum).value=true";
          $filterNum++;

          //condition
          $new = isset($_POST['New']);
          $used = isset($_POST['Used']);
          $unspec = isset($_POST['Unspecified']);

          if ($new || $used || $unspec) {
            $url .= "&itemFilter($filterNum).name=Condition";
            if ($new) {
              $url .= "&itemFilter($filterNum).value($valueNum)=New";
              $valueNum++;
            }
            if ($used) {
              $url .= "&itemFilter($filterNum).value($valueNum)=Used";
              $valueNum++;
            }
            if ($unspec) {
              $url .= "&itemFilter($filterNum).value($valueNum)=Unspecified";
              $valueNum++;
            }
          }
          

          // get JSON file from API 
          $data = file_get_contents($url); 
          $json = json_decode($data, true);
        }

        // If item was clicked then API call for item product details and similar items 
        if (isset($_POST['itemID']) || isset($_POST['simItemClicked'])) {
          if (isset($_POST['itemID'])) {
            $itemID = $_POST['itemID'];
          }
          else {
            $itemID = $_POST['simItemClicked'];
          }

          // REMOVED APP ID
          //form URL for ebay Shopping API call
          $itemURL = "http://open.api.ebay.com/shopping?callname=GetSingleItem&responseencoding=JSON&appid=&siteid=0&version=967&ItemID=$itemID&IncludeSelector=Description,Details,ItemSpecifics";

          $itemData = file_get_contents($itemURL); 
          $itemJson = json_decode($itemData, true);

          // REMOVED APP ID
          //form URL for ebay Merchandising API call
          $simItemURL = "http://svcs.ebay.com/MerchandisingService?OPERATION-NAME=getSimilarItems&SERVICE-NAME=MerchandisingService&SERVICE-VERSION=1.1.0&CONSUMER-ID=&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD&itemId=$itemID&maxResults=8";

          $simItemData = file_get_contents($simItemURL);
          $simItemJson = json_decode($simItemData, true); 
        }
      }
    ?>

    <!-- Product Search Box -->
    <div class="box">

      <h2><i>Product Search</i></h2>
      <hr style="color: lightgray;">
      
      <form id="myForm" action="" method="post" accept-charset="utf-8">
        <b>Keyword</b>
        <input type="text" id="keyword" name="keyword" value="<?php echo $_POST['keyword']; ?>" required>
        <br>
          
        <b>Category</b>
        <select name="category">
          <option value="All" <?php if($category == "All") { echo "selected"; } ?>>All Categories</option>
          <option value="Art" <?php if($category == "Art") { echo "selected"; } ?>>Art</option>
          <option value="Baby" <?php if($category == "Baby") { echo "selected"; } ?>>Baby</option>
          <option value="Books" <?php if($category == "Books") { echo "selected"; } ?>>Books</option>
          <option value="Clothing" <?php if($category == "Clothing") { echo "selected"; } ?>>Clothing, Shoes & Accessories</option>
          <option value="Computers" <?php if($category == "Computers") { echo "selected"; } ?>>Computers/Tablets & Networking</option>
          <option value="Health" <?php if($category == "Health") { echo "selected"; } ?>>Health & Beauty</option>
          <option value="Music" <?php if($category == "Music") { echo "selected"; } ?>>Music</option>
          <option value="VideoGames" <?php if($category == "VideoGames") { echo "selected"; } ?>>Video Games & Consoles</option>
          </select>
          <br>
        
          <b>Condition</b>
          <input type="checkbox" name="New" <?php if(isset($_POST['New'])) {echo "checked";} ?>>New 
          <input type="checkbox" name="Used" <?php if(isset($_POST['Used'])) {echo "checked";} ?>>Used
          <input type="checkbox" name="Unspecified" <?php if(isset($_POST['Unspecified'])) {echo "checked";} ?>>Unspecified<br>
          
          <b>Shipping Options</b>
          <input type="checkbox" name="LocalPickup" <?php if(isset($_POST['LocalPickup'])) {echo "checked";} ?>>Local Pickup 
          <input type="checkbox" name="FreeShipping" <?php if(isset($_POST['FreeShipping'])) {echo "checked";} ?>>Free Shipping<br>
          
          <input type="checkbox" id="checkSearch" name="enableSearch" onclick="searchFunction()" <?php if(isset($_POST['enableSearch'])) {echo "checked";} ?>><b>Enable Nearby Search</b> 
          
          <input type="text" id="milesFrom" name="miles" placeholder="10" size="3"  value="<?php echo $_POST['miles']; ?>" <?php if(!isset($_POST['enableSearch'])) {echo "disabled";} ?>>
          <label for="milesFrom"><b> miles from</b></label>

          <div class="zipcodeBlock">
            <input type="radio" id="fromHere" name="fromWhere" value="<?php echo $_POST['fromWhere']; ?>" checked <?php if($_POST['fromWhere'] != "fromZipcode") { echo "checked"; } ?> onclick="zipFunction()" <?php if(!isset($_POST['enableSearch'])) {echo "disabled";} ?>><label for="fromHere">Here</label><br>

            <input type="radio" id="fromZipcode" name="fromWhere" value="fromZipcode" <?php if($_POST['fromWhere'] == "fromZipcode") { echo "checked"; } ?> onclick="zipFunction()" <?php if(!isset($_POST['enableSearch'])) {echo "disabled";} ?>><input type="text" id="zipcode" name="zipcode" placeholder="zipcode" value="<?php echo $_POST['zipcode']; ?>" size="10" <?php if(!isset($_POST['fromWhere']) || $_POST['fromWhere'] != "fromZipcode") {echo "disabled";} ?> required>
          </div>
          <br>
          
          <div class="buttonBlock">
            <button type="submit" id="submitButton" name="submitButton" value="Search" disabled>Search</button>
            <button type="button" onclick="resetFunction()">Clear</button>
          </div>
        </form>
    </div>
    <br>

    <!-- Results & Error Box -->
  
    <div id="error_msg">
      <span><?php echo $zipErr ?></span>
    </div>
   
    <div id="results">
      
    </div>

    <div id="generateResults"></div>

      <script type='text/javascript'>

        // function to hide or display input object
        function hideOrShow(obj) {
          var current = document.getElementById(obj).style.display; 
          if (current == "block") {
            document.getElementById(obj).style.display = "none";
          }
          else {
            document.getElementById(obj).style.display = "block";
          }
        }

        // Resize iframe container and show/hide
        function iframeResize(){
          var frameObj = document.getElementById("sellerMsg");
          hideOrShow("sellerMsgWrap");
          // Add extra pixels to frame height to get rid of scrollbar
          if (frameObj != null) {
            var newHeight = frameObj.contentWindow.document.body.scrollHeight + 75;
            frameObj.style.height = newHeight + "px";
            frameObj.style.overflow = "hidden"; 
          }
        }

        // Wrapper function for hideOrShow for simItemsWrap
        function hideOrShowSimItems() {
          hideOrShow("simItemsWrap");
        }

        // Display search results table 
        var js_data = <?php  echo json_encode($json);?>;
        if (js_data != null) {
          var table = searchResults(js_data);
        }
        
        // Show seller message Function
        function updateMsg(clicked) {
          var text = "";
          var textID = "";
          var buttonID = "";
          var notClicked="";
          var otherContainer = "";
          if (clicked == "sellerMsgButton") {
            text = "seller message";
            textID = "showMsgTxt";
            notClicked = "simItemsButton";
            otherText = "similar items";
            otherTextID = "showSimTxt";

            otherContainer = "simItemsWrap";
          }
          else {
            text = "similar items";
            textID = "showSimTxt";
            notClicked = "sellerMsgButton";
            otherText = "seller message";
            otherTextID = "showMsgTxt";

            otherContainer = "sellerMsgWrap";

          }

          var currImg = document.getElementById(clicked).src;
          if (currImg == "http://csci571.com/hw/hw6/images/arrow_up.png") {
            //current message/items is shown (click to hide)
            document.getElementById(textID).innerHTML = "click to show " + text;
            document.getElementById(clicked).src = "http://csci571.com/hw/hw6/images/arrow_down.png";
          }
          else {
            // current message/items is hidden (click to show)
            document.getElementById(textID).innerHTML = "click to hide " + text;
            document.getElementById(clicked).src = "http://csci571.com/hw/hw6/images/arrow_up.png";
            // only show one at a time
            document.getElementById(otherTextID).innerHTML = "click to show " + otherText;
            document.getElementById(notClicked).src = "http://csci571.com/hw/hw6/images/arrow_down.png";
            document.getElementById(otherContainer).style.display = "none";
          }
        }

        // Create Item Details table
        function getItemInfo(item_jsdata, simItem_jsdata) {
          var item_info = item_jsdata.Item;
          var simItem_list = simItem_jsdata.getSimilarItemsResponse.itemRecommendations.item;

          if (item_info == undefined) {
            var html_text = "<div style='text-align:center;'><br><h4>Error retrieving Item Details.</h4></div>";
            document.getElementById("results").innerHTML = html_text;
            
          }
          else {
            // Start creating HTML text to display tables, seller message, similar items that will go in "results" div
            var html_text = "<h2>Item Details</h2><table style='width:600px;'><tbody>";

            //Photo
            if (item_info.PictureURL != undefined && item_info.PictureURL.length != 0) {
              html_text += "<tr><td width='30%'><b>Photo</b></td><td width='70%'><img src='"+ item_info.PictureURL +"' height='200px'></td></tr>";
            }
            //Title
            if (item_info.Title != undefined) {
              html_text += "<tr><td><b>Title</b></td><td>" + item_info.Title +"</td></tr>";
            }

            //Subtitle
            if (item_info.Subtitle != undefined) {
              html_text += "<tr><td><b>Subtitle</b></td><td>" + item_info.Subtitle +"</td></tr>";
            }

            //Price
            if (item_info.CurrentPrice != undefined) {
              html_text += "<tr><td><b>Price</b></td><td>$" + item_info.CurrentPrice.Value +"</td></tr>";
            }
            
            //Location
            if (item_info.Location != undefined && item_info.PostalCode != undefined) {
              html_text += "<tr><td><b>Location</b></td><td>" + item_info.Location + ", " + item_info.PostalCode +"</td></tr>";
            }
            else if (item_info.PostalCode == undefined) {
              html_text += "<tr><td><b>Location</b></td><td>" + item_info.Location + "</td></tr>";
            }
            else if (item_info.Location == undefined) {
              html_text += "<tr><td><b>Location</b></td><td>" + item_info.PostalCode + "</td></tr>";
            }

            //Seller
            if (item_info.Seller != undefined) {
              html_text += "<tr><td><b>Seller</b></td><td>" + item_info.Seller.UserID + "</td></tr>";
            }

            //Return Policy
            if (item_info.ReturnPolicy != undefined) {
              html_text += "<tr><td><b>Return Policy (US)</b></td><td>" + item_info.ReturnPolicy.ReturnsAccepted + " within " + item_info.ReturnPolicy.ReturnsWithin + "</td></tr>";
            }

            //Item Specifics
            if (item_info.ItemSpecifics != undefined) {
              var specifics = item_info.ItemSpecifics.NameValueList;
              for (var j = 0; j < specifics.length; j++) {
                html_text += "<tr><td><b>" + specifics[j].Name + "</b></td>";
                html_text += "<td>" + specifics[j].Value[0] + "</td></tr>";
              }
            }

            html_text += "</tbody></table>";
          }
          // Seller Message
          html_text += "<br><div class='imagelinks'><br><span id='showMsgTxt'>click to show seller message</span><br>";
          html_text += "<br><img src='http://csci571.com/hw/hw6/images/arrow_down.png' id='sellerMsgButton' class='imgButton' onclick='updateMsg(this.id); iframeResize();'><br>";


          html_text += "<br><div id='sellerMsgWrap' style='display:none; overflow:hidden;'>"
          if (item_info == undefined || item_info.Description == undefined || item_info.Description == "") {
            html_text +="<h4>No Seller Message Found</h4></div>";
          }
          else {
            
            html_text +="<iframe id='sellerMsg' srcdoc='"+item_info.Description+"'></iframe></div>";
          }

          // Similar Items
          html_text += "<br><span id='showSimTxt'>click to show similar items</span><br>";
          html_text += "<br><img src='http://csci571.com/hw/hw6/images/arrow_down.png' id='simItemsButton' class='imgButton' onclick='updateMsg(this.id); hideOrShowSimItems();'><br>";

          html_text += "<br><div id='simItemsWrap' style='display:none; width:700px;margin:auto;overflow-x:auto;border: 1px solid lightgray; padding:10px'><table style='border:none; width:100%;table-layout:fixed; text-align: center;'><tr>"; 

          //No similar items found
          if (simItem_list.length == 0) {
            html_text += "<td style='width:800px;'><b>No Similar Items Found.</b></td>";
          }
          else {
            
            for (var k = 0; k < simItem_list.length; k++) {
              //item image
              html_text += "<td style='border:none; width:200px; padding-left:10px; padding-right:10px'><img src='" + simItem_list[k].imageURL + "'><br>"

              //item title, which doubles as a link to item details
              html_text += "<button form='myForm' type='hidden' name='simItemClicked' onclick='itemFormSubmit()' value='"+ simItem_list[k].itemId +"' style='font-family: inherit;font-size: inherit;border: none;background-color:inherit; cursor: pointer;'>" + simItem_list[k].title + "</button><br>"

              //item price
              html_text += "<b>$" + simItem_list[k].buyItNowPrice.__value__ + "</b><br></td>";
            }
            
          }
        
          html_text += "</tr></table></div>";
          document.getElementById("results").innerHTML = html_text;
        } 

        // Display item details table
        var item_jsdata = <?php echo json_encode($itemJson); ?>;
        var simItem_jsdata = <?php echo json_encode($simItemJson); ?>; 
        if (item_jsdata != null && simItem_jsdata != null) {
          var table = getItemInfo(item_jsdata, simItem_jsdata);
        }
        
      </script>
  </body>
</html>


<!--
  Tina Li
  CS 571 
  Spring 2019
  HW 6
  -->