% P2 AND P3 ARE FALSE OPTIONAL
% Or(p1, p2, p3);
% AllOrNone(p2, p3);
% IF p1 THEN p3;

var 0..100: p1;
var 0..100: p2;
var 0..100: p3;
var 0..1: p1Set;
var 0..1: p2Set;
var 0..1: p3Set;

% Or(p1, p2, p3);
constraint (p1Set == 1) \/ (p2Set == 1) \/ (p3Set == 1);

% AllOrNone(p2, p3);
constraint (p2Set == 1) -> (p3Set == 1);
constraint (p3Set == 1) -> (p2Set == 1);
constraint (p2Set == 0) -> (p3Set == 0);
constraint (p3Set == 0) -> (p2Set == 0);

% IF p1 THEN p3;
constraint (p1Set == 1) -> (p3Set = 1);

% constraint p1Set = 0; % no es false optional
% constraint p2Set = 0; % es false optional
% constraint p3Set = 0; % es false optional

solve satisfy;