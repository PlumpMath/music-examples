(ns music-examples.example3
  (:require [score.core :refer :all]
            [score.bpf :refer :all]
            [score.freq :refer :all])  
  (:require [pink.engine :refer :all]
            [pink.config :refer :all]
            [pink.envelopes :refer [env exp-env adsr xadsr xar]]
            [pink.oscillators :refer [oscili]]
            [pink.util :refer [mul sum let-s reader const create-buffer fill]]
            [pink.instruments.horn :refer :all]
            [pink.space :refer :all]
            [pink.event :refer :all]))


(comment

  (def e (engine-create :nchnls 2))  
  (engine-start e)

  (def play (partial engine-add-afunc e))
  (def schedule (partial engine-add-events e))
  (def ->freq (comp midi->freq keyword->notenum))

  (def tempo (atom 60.0))

  (defn apply-tempo 
    [beat-time]
    (* beat-time (/ 60.0 @tempo))
    )

  (defn perf-func 
    "control-function that plays score blocks over time"
    [loc [x & xs]]
    (when x
      (play (pan 
              (mul (xar 0.01 0.05) 
                (horn 0.20 x))
              loc)) 
      (when xs
        (schedule (event perf-func (apply-tempo 0.5) loc xs)))
      
      ))

  (def score 
    (take 1000 (map ->freq (cycle [:c4 :d4]))))

  (def score2 
    (take 1000 (map ->freq (cycle [:c4 :d4 :e4 :f#4]))))

  (def score3 
    (take 1000 (map ->freq (cycle [:c5 :d5 :e5 :f#5]))))

  (schedule (event perf-func 0.0 [-1.0 score]))
  (schedule (event perf-func 0.0 [1.0 score2]))
  (schedule (event perf-func 0.0 [0.0 score3]))

  (reset! tempo 300.0)

  (engine-stop e)
  (engine-clear e)  
  (engine-kill-all)  

  )