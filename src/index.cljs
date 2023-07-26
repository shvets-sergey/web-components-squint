(ns index
  (:require ["lit" :as lit]))

(defn template-array
  [string-seq]
  (let [arr (js/Object.assign #js [] string-seq)]
    (set! (.-raw arr) string-seq)
    arr))

(js/console.log (template-array ["a" "b"]))


(defclass HelloWorld
  (extends lit/LitElement)

  (field name "Username")

  (constructor
   [this]
   (super))

  Object
  (render
   [this]
   (js/console.log "render is called" this)
   ;(js-template lit/html "<h1> Hello " name "!<h1>")
   (lit/html (template-array [ "<h1> Hello " "!</h1>"]) name)
   ))


(defn start
  []
  (js/console.log "Hello, world!")
  (.define js/customElements "hello-world" HelloWorld)
  (let [app       (.getElementById js/document "app")
        custom-el (.createElement js/document "hello-world")]
    (.appendChild app custom-el))
  )

(start)
