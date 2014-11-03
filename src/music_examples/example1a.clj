(ns pink.demo.midi-keys
  (:require [pink.simple :refer :all] 
            [pink.io.midi :refer :all]
            [pink.config :refer :all]
            [pink.space :refer :all]
            [pink.oscillators :refer :all]
            [pink.envelopes :refer [env xar]]
            [pink.filters :refer [port butterlp moogladder]]
            [pink.util :refer :all])
  (:import [javax.sound.midi MidiSystem Transmitter Receiver MidiMessage
            ShortMessage ]
           [java.util Arrays]
           [clojure.lang IFn]))

;; Example 1a - Basic Engine Use - Add/Remove Audio Functions
;;   MIDI Keyboard

(def midim (create-midi-manager))
(def keyboard (add-virtual-device midim "keyboard 1")) 

(defn saw
  [freq amp]
  (let-s [f (sum freq (mul freq 0.0025 (sine 4)))] 
    (pan (mul amp 
              ;(xar 0.005 1.0)
              (butterlp 
                (div 
                  (sum (mul 0.25 
                            (blit-saw (mul f 2.000)))
                       (blit-saw f)
                       (blit-saw (mul f 0.9995))
                       (sine2 (mul f 0.5)))
                  3.5)
                2000)) 
       0.0))) 

(comment
  ;(bind-device midim "nanoKEY KEYBOARD" "keyboard 1")
  (bind-device midim "MPKmini2" "keyboard 1")

  (bind-key-func
    keyboard 0
    (let [active (make-array IFn 128)] 
      (fn [cmd note-num velocity]
        (println ">> " cmd " " note-num " " velocity)
        (condp = cmd
          ShortMessage/NOTE_ON
          (let [afn (saw (midi->freq note-num) (/ velocity 127))]
            (aset active note-num afn)
            (add-afunc afn))                        ;; <= add-afunc

          ShortMessage/NOTE_OFF
          (when-let [afn (aget active note-num)]
            (remove-afunc afn)                      ;; <= remove-afunc 
            (aset active note-num afn)))
        )))



  (start-engine)                                    ;; start-engine
  
  )

