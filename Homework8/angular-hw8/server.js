const express = require('express');
var request = require('request');
var cors = require('cors');

const app = express();
app.use(cors());

const port = 8081;

app.listen(port, () => console.log(`App is listening on port ${port}!`));

app.use(express.static(__dirname + '/dist/angular-hw8/'));
app.get('/', (req, res) => { res.sendFile(path.join(__dirname, 'dist/angular-hw8/'));});




//GEONAMES API - zipcodes for autocomplete
app.get('/geonames/zip=:zip', function(req, res, next) { 
  getAPIJson("geonames", req.params, function(data) {
    var fulldata = JSON.parse(data); // all data from API, parsed
    var shortened = {"zipcodes" : []}; // new JSON object to put just zipcodes in
    
    //fill new JSON object with just the zipcode values
    for (var i=0; i<fulldata.postalCodes.length; i++) {
      shortened.zipcodes.push(fulldata.postalCodes[i].postalCode);
    }
    res.send(shortened); 
  });
});


//EBAY API calls
//Ebay App ID, used in all ebay api calls
const ebayAppID = ''; //REMOVED

// EBAY FINDING API for product search
app.get('/productsearch/keyword=:keyword&category=:category&new=:new&used=:used&unspecified=:unspecified&local=:local&free=:free&zipcode=:zipcode&miles=:miles', function(req, res, next) { 
  getAPIJson("productsearch", req.params, function(data) {
    res.send(JSON.parse(data));
  });
});

// EBAY SHOPPING API for product details
app.get('/productdetail/id=:id', function(req, res, next) { 
  getAPIJson("productdetail", req.params, function(data) {
    res.send(JSON.parse(data));
  });
});

// EBAY MERCHANDISING API for similar items
app.get('/similaritems/id=:id', function(req, res, next) { 
  getAPIJson("similaritems", req.params, function(data) {
    res.send(JSON.parse(data));
  });
});

// Google Custom Search API for images
const googleAPIkey = ''; //REMOVED
const searchID = ''; //REMOVED

app.get('/imagesearch/product=:product', function(req, res, next) { 
  getAPIJson("imagesearch", req.params, function(data) {
    res.send(JSON.parse(data));
  });
});


// Function to get and use the return from the http request 
// sends response data to callback function
function getAPIJson(api, myparams, callback){
  // Configure the request based on external API called
  var options = {};
  if (api == "geonames") {
    options = {
      url: `http://api.geonames.org/postalCodeSearchJSON?username=tinahli&postalcode_startsWith=${myparams.zip}&country=US&maxRows=5`,
      method: 'GET' 
    }
  }
  else if (api == "productsearch") {
    options = createEbayFindingOptions(myparams); 
  }
  else if (api == "productdetail") {
    options = {
      url: `http://open.api.ebay.com/shopping?callname=GetSingleItem&responseencoding=JSON&appid=${ebayAppID}&siteid=0&version=967&ItemID=${myparams.id}&IncludeSelector=Description,Details,ItemSpecifics`,
      method: 'GET' 
    }
  }
  else if (api == "similaritems") {
    options = {
      url: `http://svcs.ebay.com/MerchandisingService?OPERATION-NAME=getSimilarItems&SERVICE-NAME=MerchandisingService&SERVICE-VERSION=1.1.0&CONSUMER-ID=${ebayAppID}&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD&itemId=${myparams.id}&maxResults=20`,
      method: 'GET' 
    }
  }
  else if (api == "imagesearch") {
    options = {
      url: `https://www.googleapis.com/customsearch/v1?key=${googleAPIkey}&cx=${searchID}&q=${myparams.product}&imgSize=huge&searchType=image&num=8`,
      method: 'GET' 
    }
  }
  
  // Start the request
  request(options, function (error, response, body) {
  if (!error && response.statusCode == 200) {
      callback(body);
  }
  else
      console.log(error);
  })  
  
}

// create the http request options for ebay finding API call
function createEbayFindingOptions(myparams) {
  // base URL has host, app-id, json return, entries 50, and output selectors seller & store info
  var baseURL = `http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsAdvanced&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=${ebayAppID}&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD&paginationInput.entriesPerPage=50&outputSelector(0)=SellerInfo&outputSelector(1)=StoreInfo`;

  // attach category ID filter, no filter if ALL category is chosen
  if (myparams.category == "Art") {
    baseURL += "&categoryId=550";
  }
  else if (myparams.category == "Baby") {
    baseURL += "&categoryId=2984";
  }
  else if (myparams.category == "Books") {
    baseURL += "&categoryId=267";
  }
  else if (myparams.category == "Clothing") {
    baseURL += "&categoryId=11450";
  }
  else if (myparams.category == "Computers") {
    baseURL += "&categoryId=58058";
  }
  else if (myparams.category == "Health") {
    baseURL += "&categoryId=26395";
  }
  else if (myparams.category == "Music") {
    baseURL += "&categoryId=11233";
  }
  else if (myparams.category == "VideoGames") {
    baseURL += "&categoryId=1249";
  }
  
  // attach item filters to URL (they require specific format and numbering)
  var filterNum = 0;
  var valueNum = 0;

  // max distance from zipcode
  baseURL += `&itemFilter(${filterNum}).name=MaxDistance&itemFilter(${filterNum}).value=${myparams.miles}`;
  filterNum++;

  // Shipping Options
  if (myparams.free == "true") {
    baseURL += `&itemFilter(${filterNum}).name=FreeShippingOnly&itemFilter(${filterNum}).value=true`;
    filterNum++;
  }
  if (myparams.local == "true") {
    baseURL += `&itemFilter(${filterNum}).name=LocalPickupOnly&itemFilter(${filterNum}).value=true`;
    filterNum++;
  }

  //hide duplicate items
  baseURL += `&itemFilter(${filterNum}).name=HideDuplicateItems&itemFilter(${filterNum}).value=true`;
  filterNum++;

  //condition
  if (myparams.new == "true" || myparams.used == "true" || myparams.unspecified == "true") {
    baseURL += `&itemFilter(${filterNum}).name=Condition`;
    if (myparams.new == "true") {
      baseURL += `&itemFilter(${filterNum}).value(${valueNum})=New`;
      valueNum++;
    }
    if (myparams.used == "true") {
      baseURL += `&itemFilter(${filterNum}).value(${valueNum})=Used`;
      valueNum++;
    }
    if (myparams.unspecified == "true") {
      baseURL += `&itemFilter(${filterNum}).value(${valueNum})=Unspecified`;
      valueNum++;
    }
  }
  // keyword and zip added as query strings in options
  var options = {
    url: baseURL,
    method: 'GET',
    qs: {'keywords': myparams.keyword,
        'buyerPostalCode': myparams.zipcode,
    }
  }
  return options;
}