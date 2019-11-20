% P3 IS A DEAD PARAMETER
% IF p1 OR p2 THEN NOT p3;
% IF p3 THEN p4;
% IF p4 THEN p2;

var 0..100: p1;
var 0..100: p2;
var 0..100: p3;
var 0..100: p4;
var 0..1: p1Set;
var 0..1: p2Set;
var 0..1: p3Set;
var 0..1: p4Set;

% IF p1 OR p2 THEN NOT p3;
constraint (p1Set == 1 \/ p2Set == 1) -> (p3Set = 0);

% IF p3 THEN p4;
constraint (p3Set == 1) -> (p4Set = 1);

% IF p4 THEN p2;
constraint (p4Set == 1) -> (p2Set = 1);

% constraint p3Set = 1; % es dead param
% constraint p4Set = 1; % no es dead param

solve satisfy;