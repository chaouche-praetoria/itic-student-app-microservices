package cloud.praetoria.gaming.enums;

import cloud.praetoria.gaming.common.Constantes;

public enum EvalType {
	   EXERCISE(Constantes.EXERCISE_LABEL, Constantes.EXERCISE_POINTS),
	   PROJECT(Constantes.PROJECT_LABEL, Constantes.PROJECT_POINTS),
	   PARTICIPATION(Constantes.PARTICIPATION_LABEL, Constantes.PARTICIPATION_POINTS),
	   QUIZ(Constantes.QUIZ_LABEL, Constantes.QUIZ_POINTS),
	   EXAM(Constantes.EXAM_LABEL, Constantes.EXAM_POINTS),
	   LAB(Constantes.LAB_LABEL, Constantes.LAB_POINTS),
	   PRESENTATION(Constantes.PRESENTATION_LABEL, Constantes.PRESENTATION_POINTS);

	private final String libelle;
	private final Integer pointsMaximumDefaut;

	EvalType(String libelle, Integer pointsMaximumDefaut) {
		this.libelle = libelle;
		this.pointsMaximumDefaut = pointsMaximumDefaut;
	}

	public String getLibelle() {
		return libelle;
	}

	public Integer getPointsMaximumDefaut() {
		return pointsMaximumDefaut;
	}
}
