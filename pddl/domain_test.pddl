(define (domain logistics)

  (:requirements :strips :typing)

  (:types object
  )

  (:predicates 	(on ?x ?y - object) (free) (busy)
    )

  (:action take
    :parameters (?x - object)
    :precondition (and (free))
    :effect (and (not (free)) (busy))
  )
)
