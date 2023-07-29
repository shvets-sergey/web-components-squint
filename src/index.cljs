(ns index
  (:require ["lit" :as lit]))

(defn template-array
  [string-seq]
  (let [arr (js/Object.assign #js [] string-seq)]
    (set! (.-raw arr) string-seq)
    arr))

(js/console.log (template-array ["a" "b"]))

#_:clj-kondo/ignore
(defclass HelloWorld
  (extends lit/LitElement)

  (field fname "Username")

  (constructor
   [this]
   (super)
   )

  Object
  (render
   [this]
   (js/console.log "render is called" this)
   ;(js-template lit/html "<h1> Hello " name "!<h1>")
   (lit/html (template-array [ "<h1> Hello " "!</h1>"]) fname)
   ))


(.define js/customElements "hello-world" HelloWorld)

#_:clj-kondo/ignore
(defclass HelloWorld2
  (extends lit/LitElement)

  (field fname "Username 2")
  (field ^boolean checked false)

  (constructor
   [this]
   (super))

  Object
  (render
   [this]
   (js/console.log "render is called" this)
  ;;  (js-template lit/html "<div><h1> Hello " fname "!</h1>"
  ;;               "<input placeholder=\"Enter your name\" @input=" (.-handle-click this) " ?disabled=" (not checked) "/>"
  ;;               "<label><input type=\"checkbox\" @change=" (.-set-checked this) ">Enable Editing</label>" "</div>")
   (lit/html (template-array ["<div><h1> Hello " fname "!</h1>"
                              ;; "<input placeholder=\"Enter your name\" @input=" (.-handle-click this) " ?disabled=" (not checked) "/>"
                              ;; "<label><input type=\"checkbox\" @change=" (.-set-checked this) ">Enable Editing</label>" "</div>"
                              ]))
   )

  (handle-click
   [this event]
   (js/console.log "click" this)
   (js/console.log "event" event)
   (set! fname (.. event -target -value))
   nil)

  (set-checked
   [this event]
   (js/console.log "Setting checkbox value" event)
   (set! checked (.. event -target -checked)))

  (connectedCallback
   [this]
   ;; Object.getPrototypeOf(Dog.prototype).speak.call(this);
   (super (connectedCallback))
   ;;(.connectedCallback (super))
   ;;(js* "super.connectedCallback()")
   (js/console.log "Hello from Dom" this)))


(set! (.-properties HelloWorld2) #js {"fname" #js {} "checked" #js {}})
(.define js/customElements "hello-world-2" HelloWorld2)


(defn add-component-to-app
  [comp-str]
  (let [app       (.getElementById js/document "app")
        custom-el (.createElement js/document comp-str)]
    (.appendChild app custom-el)))

(defn start
  []
  (js/console.log "Hello, world!")
  (add-component-to-app "hello-world")
  (add-component-to-app "hello-world-2")
  )

(start)
