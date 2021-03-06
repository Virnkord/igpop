(ns igpop.site.docs
  (:require
   [igpop.site.views :as views]
   [clojure.string :as str]
   [garden.core :as gc]
   [markdown.core :as md]))


(defn current-page [uri res-url]
  (= uri res-url))

(defn menu [ctx {uri :uri}]
  [:div#main-menu
   (for [[doc-id doc] (:pages (:docs ctx))]
     (let [res-url (str "/docs/" (name doc-id))]
       [:a {:href res-url :class (when (current-page uri res-url) "active")} (or (:title doc) doc-id)]))])

(def styles
  [:body])

(def style-tag [:style (gc/css styles)])

(defn dashboard [ctx req]
  {:status 200
   :body (views/layout
          ctx
          style-tag
          (menu ctx req)
          [:div#content]
          #_(into [:div#content]
                (->> (get-in ctx [:docs :pages])
                     (sort-by first)
                     (mapv (fn [[doc-id doc]]
                             [:pre (pr-str doc-id doc)])))))})

(defn doc-page [ctx {{doc-id :doc-id} :route-params :as req}]
  (let [doc (get-in ctx [:docs :pages (keyword doc-id)])]
    {:status 200
     :body (views/layout
            ctx
            style-tag
            (menu ctx req)
            [:div#content
             [:pre (pr-str (dissoc doc :source))]
             (markdown.core/md-to-html-string (:source doc))])}))
