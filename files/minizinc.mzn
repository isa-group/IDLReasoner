var 0..1: alt;
var 0..1: altSet;
var 0..1: fields;
var 0..1: fieldsSet;
var 0..1: key;
var 0..1: keySet;
var 0..1: oauth_token;
var 0..1: oauth_tokenSet;
var bool: prettyPrint;
var 0..1: prettyPrintSet;
var 0..1: quotaUser;
var 0..1: quotaUserSet;
var 0..1: userIp;
var 0..1: userIpSet;
var 0..1: chart;
var 0..1: chartSet;
var 0..1: hl;
var 0..1: hlSet;
var 0..1: id;
var 0..1: idSet;
var 0..1: locale;
var 0..1: localeSet;
var 0..1: maxHeight;
var 0..1: maxHeightSet;
var 0..1: maxResults;
var 0..1: maxResultsSet;
var 0..1: maxWidth;
var 0..1: maxWidthSet;
var 0..1: myRating;
var 0..1: myRatingSet;
var 0..1: onBehalfOfContentOwner;
var 0..1: onBehalfOfContentOwnerSet;
var 0..1: pageToken;
var 0..1: pageTokenSet;
var 0..1: part;
var 0..1: partSet;
var 0..1: regionCode;
var 0..1: regionCodeSet;
var 0..1: videoCategoryId;
var 0..1: videoCategoryIdSet;

constraint ((((chartSet==1)) -> ((not ((idSet==1))) /\ (not ((myRatingSet==1))))) /\ (((idSet==1)) -> ((not ((chartSet==1))) /\ (not ((myRatingSet==1))))) /\ (((myRatingSet==1)) -> ((not ((chartSet==1))) /\ (not ((idSet==1))))) /\ (((chartSet==1)) \/ ((idSet==1)) \/ ((myRatingSet==1))));
%---------------------
constraint ((((maxResultsSet==1)) -> ((not ((idSet==1))))) /\ (((idSet==1)) -> ((not ((maxResultsSet==1))))));
%---------------------
constraint ((((pageTokenSet==1)) -> ((not ((idSet==1))))) /\ (((idSet==1)) -> ((not ((pageTokenSet==1))))));
%---------------------
constraint ((regionCodeSet==1)) -> ((chartSet==1));
%---------------------
constraint ((videoCategoryIdSet==1)) -> ((chartSet==1));
%---------------------
constraint alt == 1;
constraint altSet == 1;
solve satisfy;