# Android Web App Container

Yet another hybrid framework; this time one that attempts to retain key aspects of Android's activity model but allows the app to be developed using web technologies.

Initially this is just a tool to help quickly and easily prototype/demonstrate what a mobile app can do by accessing rich and dynamic data via third-party APIs.

The `R.string.web_app_url` resource points to a root page, for an example see http://acuth.github.io/test/home.html. This needs to pull in the Javascript definition of Awac from http://acuth.github.io/js/awac.js and initialise a Javascript variable to represent the container when the page has loaded and then call the `showPage()` method. For example:

```javascript
var awac = null;
$(document).ready(function() { awac = new Awac('awac'); awac.showPage(); });
```
Thereafter the container can be asked to perform certain native operations, for example opening a page as a new activity `awac.newPage('highline');` which will display the page http://acuth.github.io/test/highline.html
