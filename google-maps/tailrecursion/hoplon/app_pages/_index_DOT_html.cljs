(ns tailrecursion.hoplon.app-pages._index_DOT_html (:require [hoplon.google.api :as g :refer [google-map]] [hoplon.twitter.bootstrap :as b :refer [container tab tabs]] [tailrecursion.javelin :refer [alts! cell-map propagate! next-rank deref* lift cell input? cell-doseq* bf-seq destroy-cell! last-rank set-cell! set-formula! cell?]] [tailrecursion.hoplon :refer [script do! track article noscript command h4 h3 mark basefont h5 span input h2 th label h6 pre nav vector?* address sup h1 table font option datalist u safe-nth on! footer select q samp source summary li p td noframes node? iframe rel tr s *initfns* add-attributes! colgroup relx html dfn optgroup tbody text-val! ul hgroup sub strong data progress loop-tpl* acronym append-child replace-children! img details fieldset html-head em html-time rt when-dom video keygen div val-id dt ol link init form is-ie8 check-val! menu timeout del a parse-args area legend hr dir header param meter tfoot blockquote eventsource b dl figcaption caption route-cell style rel-event abbr ruby applet html-meta bdi embed rp figure on-append! canvas section object strike title button output audio initialized? add-children! dd bdo cite code kbd big seq?* frame rely col tt i ins thead unsplice isindex frameset spliced base $text by-id $comment br textarea wbr html-map small add-initfn! html-body aside html-var]]) (:require-macros [tailrecursion.javelin :refer [cell-doseq cell= defc defc= mx with-let prop-cell mx2 cell-let set-cell!= macroexpand-all]] [tailrecursion.hoplon :refer [with-init! body text defelem loop-tpl head with-timeout with-interval def-values flatten-expr]]))

(clojure.core/defn ^:export hoploninit [] (reset! g/api-key "AIzaSyA3SuTQQlfhLL0O--JS91QqUkKBJp6HE7g") (def miami-beach {:name "Miami Beach", :lat 25.813, :lon -80.1341}) (def san-francisco {:name "San Francisco", :lat 37.7833, :lon -122.4167}) (defelem info [{:keys [title body]} _] (div :css {:height "100px", :width "300px"} (h4 title) (hr) (p body))) (defn rand-pin [{:keys [name lat lon]}] (let [perturb! (fn* [p1__6746#] (. (+ p1__6746# (* 0.03 (- (rand) 0.5))) (toFixed 4))) [lat lon] (map perturb! [lat lon]) name (str "A place in " name) content (str "Coordinates: " lat "ºN, " lon "ºW.")] {:lat lat, :lon lon, :info (info :title name :body content)})) (defn rand-pins [city n] (vec (take n (repeatedly (fn* [] (rand-pin city)))))) (defn do-steps [msec [step & steps]] (with-timeout msec (when step (step)) (when steps (do-steps msec steps)))) (defn bounce-marker [marker] (.setAnimation marker (.. js/google -maps -Animation -BOUNCE)) (with-timeout 1400 (.setAnimation marker nil))) (defc auto? true) (defc opts {:zoom 8}) (defc pins (rand-pins san-francisco 4)) (with-init! (with-timeout 1000 (do-steps 1500 [(fn* [] (reset! pins (rand-pins san-francisco 4))) (fn* [] (reset! pins (rand-pins san-francisco 4))) (fn* [] (reset! pins (rand-pins san-francisco 4))) (fn* [] (swap! auto? not)) (fn* [] (swap! pins pop)) (fn* [] (swap! pins pop)) (fn* [] (swap! pins conj (rand-pin san-francisco))) (fn* [] (swap! pins conj (rand-pin san-francisco))) (fn* [] (swap! auto? not)) (fn* [] (swap! pins into (rand-pins miami-beach 5))) (fn* [] (swap! pins subvec 4))]))) (html :lang "en" (head (title "Hoplon • Google Maps")) (body (container (h1 "Hoplon & Google Maps") (hr) (tabs (tab :name "First Map" (google-map :css {:height "600px", :width "100%", :margin "0 auto", :border "1px solid red"} :center san-francisco :pins pins :pin-click (fn* [p1__6748# p2__6747#] (bounce-marker p2__6747#)) :map-click (fn* [p1__6749#] (print :map-clicked p1__6749#)) :fit-pins auto? :opts opts)) (tab :name "Second Map" (google-map :css {:height "600px", :width "100%", :margin "0 auto", :border "1px solid blue"} :center san-francisco :pins (clojure.core/deref pins) :opts {:zoom 12}))) (hr) (p :css {:text-align "center"} (a :href "https://github.com/tailrecursion/hoplon-demos/blob/master/google-maps/src/index.cljs.hl" "source code"))))) (tailrecursion.hoplon/init))