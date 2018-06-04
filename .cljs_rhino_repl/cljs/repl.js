// Compiled by ClojureScript 1.10.238 {}
goog.provide('cljs.repl');
goog.require('cljs.core');
goog.require('cljs.spec.alpha');
cljs.repl.print_doc = (function cljs$repl$print_doc(p__51479){
var map__51480 = p__51479;
var map__51480__$1 = ((((!((map__51480 == null)))?(((((map__51480.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51480.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51480):map__51480);
var m = map__51480__$1;
var n = cljs.core.get.call(null,map__51480__$1,new cljs.core.Keyword(null,"ns","ns",441598760));
var nm = cljs.core.get.call(null,map__51480__$1,new cljs.core.Keyword(null,"name","name",1843675177));
cljs.core.println.call(null,"-------------------------");

cljs.core.println.call(null,[cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var temp__5457__auto__ = new cljs.core.Keyword(null,"ns","ns",441598760).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__5457__auto__)){
var ns = temp__5457__auto__;
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(ns),"/"].join('');
} else {
return null;
}
})()),cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m))].join(''));

if(cljs.core.truth_(new cljs.core.Keyword(null,"protocol","protocol",652470118).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Protocol");
} else {
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"forms","forms",2045992350).cljs$core$IFn$_invoke$arity$1(m))){
var seq__51482_51504 = cljs.core.seq.call(null,new cljs.core.Keyword(null,"forms","forms",2045992350).cljs$core$IFn$_invoke$arity$1(m));
var chunk__51483_51505 = null;
var count__51484_51506 = (0);
var i__51485_51507 = (0);
while(true){
if((i__51485_51507 < count__51484_51506)){
var f_51508 = cljs.core._nth.call(null,chunk__51483_51505,i__51485_51507);
cljs.core.println.call(null,"  ",f_51508);


var G__51509 = seq__51482_51504;
var G__51510 = chunk__51483_51505;
var G__51511 = count__51484_51506;
var G__51512 = (i__51485_51507 + (1));
seq__51482_51504 = G__51509;
chunk__51483_51505 = G__51510;
count__51484_51506 = G__51511;
i__51485_51507 = G__51512;
continue;
} else {
var temp__5457__auto___51513 = cljs.core.seq.call(null,seq__51482_51504);
if(temp__5457__auto___51513){
var seq__51482_51514__$1 = temp__5457__auto___51513;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__51482_51514__$1)){
var c__4319__auto___51515 = cljs.core.chunk_first.call(null,seq__51482_51514__$1);
var G__51516 = cljs.core.chunk_rest.call(null,seq__51482_51514__$1);
var G__51517 = c__4319__auto___51515;
var G__51518 = cljs.core.count.call(null,c__4319__auto___51515);
var G__51519 = (0);
seq__51482_51504 = G__51516;
chunk__51483_51505 = G__51517;
count__51484_51506 = G__51518;
i__51485_51507 = G__51519;
continue;
} else {
var f_51520 = cljs.core.first.call(null,seq__51482_51514__$1);
cljs.core.println.call(null,"  ",f_51520);


var G__51521 = cljs.core.next.call(null,seq__51482_51514__$1);
var G__51522 = null;
var G__51523 = (0);
var G__51524 = (0);
seq__51482_51504 = G__51521;
chunk__51483_51505 = G__51522;
count__51484_51506 = G__51523;
i__51485_51507 = G__51524;
continue;
}
} else {
}
}
break;
}
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"arglists","arglists",1661989754).cljs$core$IFn$_invoke$arity$1(m))){
var arglists_51525 = new cljs.core.Keyword(null,"arglists","arglists",1661989754).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_((function (){var or__3922__auto__ = new cljs.core.Keyword(null,"macro","macro",-867863404).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(or__3922__auto__)){
return or__3922__auto__;
} else {
return new cljs.core.Keyword(null,"repl-special-function","repl-special-function",1262603725).cljs$core$IFn$_invoke$arity$1(m);
}
})())){
cljs.core.prn.call(null,arglists_51525);
} else {
cljs.core.prn.call(null,((cljs.core._EQ_.call(null,new cljs.core.Symbol(null,"quote","quote",1377916282,null),cljs.core.first.call(null,arglists_51525)))?cljs.core.second.call(null,arglists_51525):arglists_51525));
}
} else {
}
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"special-form","special-form",-1326536374).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Special Form");

cljs.core.println.call(null," ",new cljs.core.Keyword(null,"doc","doc",1913296891).cljs$core$IFn$_invoke$arity$1(m));

if(cljs.core.contains_QMARK_.call(null,m,new cljs.core.Keyword(null,"url","url",276297046))){
if(cljs.core.truth_(new cljs.core.Keyword(null,"url","url",276297046).cljs$core$IFn$_invoke$arity$1(m))){
return cljs.core.println.call(null,["\n  Please see http://clojure.org/",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"url","url",276297046).cljs$core$IFn$_invoke$arity$1(m))].join(''));
} else {
return null;
}
} else {
return cljs.core.println.call(null,["\n  Please see http://clojure.org/special_forms#",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m))].join(''));
}
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"macro","macro",-867863404).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Macro");
} else {
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"repl-special-function","repl-special-function",1262603725).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"REPL Special Function");
} else {
}

cljs.core.println.call(null," ",new cljs.core.Keyword(null,"doc","doc",1913296891).cljs$core$IFn$_invoke$arity$1(m));

if(cljs.core.truth_(new cljs.core.Keyword(null,"protocol","protocol",652470118).cljs$core$IFn$_invoke$arity$1(m))){
var seq__51486_51526 = cljs.core.seq.call(null,new cljs.core.Keyword(null,"methods","methods",453930866).cljs$core$IFn$_invoke$arity$1(m));
var chunk__51487_51527 = null;
var count__51488_51528 = (0);
var i__51489_51529 = (0);
while(true){
if((i__51489_51529 < count__51488_51528)){
var vec__51490_51530 = cljs.core._nth.call(null,chunk__51487_51527,i__51489_51529);
var name_51531 = cljs.core.nth.call(null,vec__51490_51530,(0),null);
var map__51493_51532 = cljs.core.nth.call(null,vec__51490_51530,(1),null);
var map__51493_51533__$1 = ((((!((map__51493_51532 == null)))?(((((map__51493_51532.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51493_51532.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51493_51532):map__51493_51532);
var doc_51534 = cljs.core.get.call(null,map__51493_51533__$1,new cljs.core.Keyword(null,"doc","doc",1913296891));
var arglists_51535 = cljs.core.get.call(null,map__51493_51533__$1,new cljs.core.Keyword(null,"arglists","arglists",1661989754));
cljs.core.println.call(null);

cljs.core.println.call(null," ",name_51531);

cljs.core.println.call(null," ",arglists_51535);

if(cljs.core.truth_(doc_51534)){
cljs.core.println.call(null," ",doc_51534);
} else {
}


var G__51536 = seq__51486_51526;
var G__51537 = chunk__51487_51527;
var G__51538 = count__51488_51528;
var G__51539 = (i__51489_51529 + (1));
seq__51486_51526 = G__51536;
chunk__51487_51527 = G__51537;
count__51488_51528 = G__51538;
i__51489_51529 = G__51539;
continue;
} else {
var temp__5457__auto___51540 = cljs.core.seq.call(null,seq__51486_51526);
if(temp__5457__auto___51540){
var seq__51486_51541__$1 = temp__5457__auto___51540;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__51486_51541__$1)){
var c__4319__auto___51542 = cljs.core.chunk_first.call(null,seq__51486_51541__$1);
var G__51543 = cljs.core.chunk_rest.call(null,seq__51486_51541__$1);
var G__51544 = c__4319__auto___51542;
var G__51545 = cljs.core.count.call(null,c__4319__auto___51542);
var G__51546 = (0);
seq__51486_51526 = G__51543;
chunk__51487_51527 = G__51544;
count__51488_51528 = G__51545;
i__51489_51529 = G__51546;
continue;
} else {
var vec__51495_51547 = cljs.core.first.call(null,seq__51486_51541__$1);
var name_51548 = cljs.core.nth.call(null,vec__51495_51547,(0),null);
var map__51498_51549 = cljs.core.nth.call(null,vec__51495_51547,(1),null);
var map__51498_51550__$1 = ((((!((map__51498_51549 == null)))?(((((map__51498_51549.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51498_51549.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51498_51549):map__51498_51549);
var doc_51551 = cljs.core.get.call(null,map__51498_51550__$1,new cljs.core.Keyword(null,"doc","doc",1913296891));
var arglists_51552 = cljs.core.get.call(null,map__51498_51550__$1,new cljs.core.Keyword(null,"arglists","arglists",1661989754));
cljs.core.println.call(null);

cljs.core.println.call(null," ",name_51548);

cljs.core.println.call(null," ",arglists_51552);

if(cljs.core.truth_(doc_51551)){
cljs.core.println.call(null," ",doc_51551);
} else {
}


var G__51553 = cljs.core.next.call(null,seq__51486_51541__$1);
var G__51554 = null;
var G__51555 = (0);
var G__51556 = (0);
seq__51486_51526 = G__51553;
chunk__51487_51527 = G__51554;
count__51488_51528 = G__51555;
i__51489_51529 = G__51556;
continue;
}
} else {
}
}
break;
}
} else {
}

if(cljs.core.truth_(n)){
var temp__5457__auto__ = cljs.spec.alpha.get_spec.call(null,cljs.core.symbol.call(null,[cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.ns_name.call(null,n))].join(''),cljs.core.name.call(null,nm)));
if(cljs.core.truth_(temp__5457__auto__)){
var fnspec = temp__5457__auto__;
cljs.core.print.call(null,"Spec");

var seq__51500 = cljs.core.seq.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"args","args",1315556576),new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Keyword(null,"fn","fn",-1175266204)], null));
var chunk__51501 = null;
var count__51502 = (0);
var i__51503 = (0);
while(true){
if((i__51503 < count__51502)){
var role = cljs.core._nth.call(null,chunk__51501,i__51503);
var temp__5457__auto___51557__$1 = cljs.core.get.call(null,fnspec,role);
if(cljs.core.truth_(temp__5457__auto___51557__$1)){
var spec_51558 = temp__5457__auto___51557__$1;
cljs.core.print.call(null,["\n ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name.call(null,role)),":"].join(''),cljs.spec.alpha.describe.call(null,spec_51558));
} else {
}


var G__51559 = seq__51500;
var G__51560 = chunk__51501;
var G__51561 = count__51502;
var G__51562 = (i__51503 + (1));
seq__51500 = G__51559;
chunk__51501 = G__51560;
count__51502 = G__51561;
i__51503 = G__51562;
continue;
} else {
var temp__5457__auto____$1 = cljs.core.seq.call(null,seq__51500);
if(temp__5457__auto____$1){
var seq__51500__$1 = temp__5457__auto____$1;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__51500__$1)){
var c__4319__auto__ = cljs.core.chunk_first.call(null,seq__51500__$1);
var G__51563 = cljs.core.chunk_rest.call(null,seq__51500__$1);
var G__51564 = c__4319__auto__;
var G__51565 = cljs.core.count.call(null,c__4319__auto__);
var G__51566 = (0);
seq__51500 = G__51563;
chunk__51501 = G__51564;
count__51502 = G__51565;
i__51503 = G__51566;
continue;
} else {
var role = cljs.core.first.call(null,seq__51500__$1);
var temp__5457__auto___51567__$2 = cljs.core.get.call(null,fnspec,role);
if(cljs.core.truth_(temp__5457__auto___51567__$2)){
var spec_51568 = temp__5457__auto___51567__$2;
cljs.core.print.call(null,["\n ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name.call(null,role)),":"].join(''),cljs.spec.alpha.describe.call(null,spec_51568));
} else {
}


var G__51569 = cljs.core.next.call(null,seq__51500__$1);
var G__51570 = null;
var G__51571 = (0);
var G__51572 = (0);
seq__51500 = G__51569;
chunk__51501 = G__51570;
count__51502 = G__51571;
i__51503 = G__51572;
continue;
}
} else {
return null;
}
}
break;
}
} else {
return null;
}
} else {
return null;
}
}
});

//# sourceMappingURL=repl.js.map
