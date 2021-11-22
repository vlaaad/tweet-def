(ns io.github.vlaaad.tweet-def
  (:require [clojure.string :as str]
            [clojure.data.json :as json]
            [clj-http.client :as http])
  (:import [org.apache.commons.text StringEscapeUtils]))

(defn- id [tweet-url]
  (or (second (re-find #"status/(.+)" tweet-url))
      (throw (ex-info "Can't extract tweet id" {:url tweet-url}))))

(def bearer
  (delay
    (let [response (http/request
                     {:method :get
                      :cookie-policy :none
                      :url "https://abs.twimg.com/responsive-web/client-web/main.90f9e505.js"})]
      (or (->> response
               :body
               (re-find #"s=\"(AAAAA[^\"]+)\"")
               second)
          (throw (ex-info "Can't get auth bearer" {:response response}))))))

(def guest-token
  (delay
    (let [response (http/request
                     {:method :post
                      :cookie-policy :none
                      :headers {"Authorization" (str "Bearer " @bearer)}
                      :url "https://api.twitter.com/1.1/guest/activate.json"})]
      (-> response
          :body
          (json/read-str :key-fn keyword)
          :guest_token
          (or (throw (ex-info "Can't get guest token" {:response response})))))))

(defn- load-tweet [tweet-url]
  (let [id (id tweet-url)
        response (http/request
                   {:method :get
                    :cookie-policy :none
                    :headers {"Authorization" (str "Bearer " @bearer)
                              "x-guest-token" @guest-token}
                    :url (str "https://api.twitter.com/1.1/statuses/show/" id ".json?tweet_mode=extended")})]
    (-> response
        :body
        (json/read-str :key-fn keyword)
        :full_text
        (or (throw (ex-info "Can't load tweet" {:response response})))
        StringEscapeUtils/unescapeHtml4)))

(defn def [tweet-url]
  (let [tweet (load-tweet tweet-url)
        i (or (str/index-of tweet "(def")
              (throw (ex-info "Can't find code definition in tweet" {:tweet tweet})))]
    (eval (read-string (subs tweet i)))))

(comment
  (io.github.vlaaad.tweet-def/def "https://twitter.com/cgrand/status/1281527501387440128")
  (penultimate [1 2 3]) => 2)