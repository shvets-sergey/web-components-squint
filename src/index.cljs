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
   (js-template lit/html "<div><h1> Hello " fname "!</h1>"
                "<input placeholder=\"Enter your name\" @input=" (.-handleClick this) " ?disabled=" (not checked) "/>"
                "<label><input type=\"checkbox\" @change=" (.-setChecked this) ">Enable Editing</label>" "</div>"))

  (handleClick
   [this event]
   (js/console.log "click" this)
   (js/console.log "event" event)
   (set! fname (.. event -target -value))
   nil)

  (setChecked
   [this event]
   (js/console.log "Setting checkbox value" event)
   (set! checked (.. event -target -checked)))

  (connectedCallback
   [this]
   (.connectedCallback (js* "super"))
   (js/console.log "Hello from Dom" this)))


(set! (.-properties HelloWorld2) #js {"fname" #js {} "checked" #js {}})
(.define js/customElements "hello-world-2" HelloWorld2)


(defn js-map
  [f js-seq]
  (let [jsArr #js []]
    (doseq [item js-seq]
      (let [processed (f item)]
        (.push jsArr processed)))
    jsArr))


(defn js-filter
  [f js-seq]
  (let [jsArr #js []]
    (doseq [item js-seq]
      (when (f item)
        (.push jsArr item)))
    jsArr))


#_:clj-kondo/ignore
(defclass ToDoList
  (extends lit/LitElement)

  (field listItems #js {"state" true})
  (field ^boolean hideCompleted false)

  (constructor
   [this]
   (super)
   (set! listItems #js [#js {"text"      "Start Lit tutorial"
                             "completed" true}
                        #js {"text"      "Make to-do list"
                             "completed" false}]))

  Object
  (render
   [this]
   (js/console.log "render todos" hideCompleted)
   (let [todo-render (fn [todos]
                       (if (zero? (.-length todos))
                         (js-template lit/html "<h1> All Done! Take a cake! </h1>")
                         (js-map (fn [^js item]
                                   (js-template lit/html "<li class=" (if (.-completed item) "completed" "") " @click=" (fn [] (.toggleTodo this item)) ">"
                                                (.-text item) "</li>"))
                                 todos)))]
     (js-template lit/html
                  "<h2> ToDo List</h2>"
                  "<ul>"
                  (todo-render (if hideCompleted
                                 (js-filter (fn [^js item]
                                              (false? (.-completed item)))
                                            listItems)
                                 listItems))
                  "</ul>"
                  "<input id=\"newItem\" aria-label=\"New Item\">"
                  "<button @click=" (.-addTodo this) "> Add </button>"
                  "<label><input type=\"checkbox\" @change=" (.-toggleCompletedTodos this) ">Hide Completed</label>" "</div>")))

  (todoRef
   [this]
   (some-> (.-renderRoot this)
           (.querySelector "#newItem")))

  (toggleCompletedTodos
   [this event]
   (set! hideCompleted (.. event -target -checked)))


  (toggleTodo
   [this ^js item]
   (js/console.log "toggling" item)
   (set! (.-completed item) (not (.-completed item)))
   (.requestUpdate this))


  (addTodo
   [this]
   (js/console.log "Add todo clicked"  (.-value (.todoRef this)))
   (let [cloned-arr (.slice listItems)]
     (.push cloned-arr #js {"text" (.-value (.todoRef this)) "completed" false})
     (js/console.log cloned-arr)
     (set! listItems cloned-arr))
   nil))


(set! (.-properties ToDoList) #js {"listItems" #js {"state" true}
                                   "hideCompleted" #js {}})

(set! (.-styles ToDoList) (js-template lit/css "
                                                .completed {
    text-decoration-line: line-through;
    color: #777;
  }"))

(.define js/customElements "todo-list" ToDoList)

#_:clj-kondo/ignore
(defclass ToDoListCljs
  (extends lit/LitElement)

  (field listItems #js {"state" true})
  (field ^boolean hideCompleted false)

  (constructor
   [this]
   (super)
   (set! listItems [{:text      "Transform to cljs"
                     :completed true}
                    {:text      "Celebrate with beer"
                     :completed false}]))

  Object
  (render
   [this]
   (js/console.log "render todos" hideCompleted)
   (let [todo-render (fn [todos]
                       (if (zero? (count todos))
                         (js-template lit/html "<h1> All Done! Take a cake! </h1>")
                         (js-map (fn [item]
                                   (js-template lit/html "<li class=" (if (:completed item) "completed" "") " @click=" (fn [] (.toggleTodo this item)) ">"
                                                (:text item) "</li>"))
                                 todos)))]
     (js-template lit/html
                  "<h2> ToDo List CLJS</h2>"
                  "<ul>"
                  (todo-render (if hideCompleted
                                 (filter (fn [item]
                                           (false? (:completed item)))
                                         listItems)
                                 listItems))
                  "</ul>"
                  "<input id=\"newItem\" aria-label=\"New Item\">"
                  "<button @click=" (.-addTodo this) "> Add </button>"
                  "<label><input type=\"checkbox\" @change=" (.-toggleCompletedTodos this) ">Hide Completed</label>" "</div>")))

  (todoRef
   [this]
   (some-> (.-renderRoot this)
           (.querySelector "#newItem")))

  (toggleCompletedTodos
   [this event]
   (set! hideCompleted (.. event -target -checked)))


  (toggleTodo
   [this ^js item]
   (js/console.log "toggling" item)
   (set! listItems (mapv (fn [stored-item]
                           (if (= (:text item) (:text stored-item))
                             (update stored-item :completed not)
                             stored-item))
                         listItems)))


  (addTodo
   [this]
   (js/console.log "Add todo clicked"  (.-value (.todoRef this)))

   (set! listItems
         (conj listItems {:text      (.-value (.todoRef this))
                          :completed false}))
   nil))

(set! (.-properties ToDoListCljs) #js {"listItems" #js {"state" true}
                                       "hideCompleted" #js {}})
(set! (.-styles ToDoListCljs) (js-template lit/css "
                                                .completed {
    text-decoration-line: line-through;
    color: #777;
  }"))

(.define js/customElements "todo-list-cljs" ToDoListCljs)

;; #_:clj-kondo/ignore
;; (defclass ToDoListAtom
;;   (extends lit/LitElement)

;;   (field state)

;;   (constructor
;;    [this]
;;    (super)
;;    (let [state* (atom {:todos           [{:text      "Transform to atom"
;;                                           :completed true}
;;                                          {:text      "Celebrate with beer"
;;                                           :completed false}]
;;                        :hide-completed? false})]
;;      (add-watch state* :ui-update #(.requestUpdate this))
;;      (set! state state*)))

;;   Object
;;   (render
;;    [this]
;;    (js/console.log "render todos" (:hide-completed? @state))
;;    (let [todo-render (fn [todos]
;;                        (if (zero? (count todos))
;;                          (js-template lit/html "<h1> All Done! Take a cake! </h1>")
;;                          (js-map (fn [item]
;;                                    (js-template lit/html "<li class=" (if (:completed item) "completed" "") " @click=" (fn [] (.toggleTodo this item)) ">"
;;                                                 (:text item) "</li>"))
;;                                  todos)))]
;;      (js-template lit/html
;;                   "<h2> ToDo List Atom</h2>"
;;                   "<ul>"
;;                   (todo-render (if (:hide-completed? @state)
;;                                  (filter (fn [item]
;;                                            (false? (:completed item)))
;;                                          (:todos @state))
;;                                  (:todos @state)))
;;                   "</ul>"
;;                   "<input id=\"newItem\" aria-label=\"New Item\">"
;;                   "<button @click=" (.-addTodo this) "> Add </button>"
;;                   "<label><input type=\"checkbox\" @change=" (.-toggleCompletedTodos this) ">Hide Completed</label>" "</div>")))

;;   (todoRef
;;    [this]
;;    (some-> (.-renderRoot this)
;;            (.querySelector "#newItem")))

;;   (toggleCompletedTodos
;;    [this event]
;;    (swap! state update :hide-completed? not))


;;   (toggleTodo
;;    [this ^js item]
;;    (js/console.log "toggling" item)
;;    (swap! state update :todos
;;           (fn [todos]
;;             (mapv (fn [stored-item]
;;                     (if (= (:text item) (:text stored-item))
;;                       (update stored-item :completed not)
;;                       stored-item))
;;                   todos))))


;;   (addTodo
;;    [this]
;;    (js/console.log "Add todo clicked"  (.-value (.todoRef this)))

;;    (swap! state update :todos
;;           (fn [todos]
;;             (conj todos {:text (.-value (.todoRef this)) :completed false})))
;;    nil))

;; (set! (.-styles ToDoListAtom) (js-template lit/css "
;;                                                 .completed {
;;     text-decoration-line: line-through;
;;     color: #777;
;;   }"))

;; (.define js/customElements "todo-list-atom" ToDoListAtom)

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
  (add-component-to-app "todo-list")
  (add-component-to-app "todo-list-cljs")
  ;(add-component-to-app "todo-list-atom")
  )

(start)
