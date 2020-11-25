/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enveloppeconvexe;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Nicol
 */
public class ConvexHull {

	private static enum VECTDIR {
		HORAIRE, TRIGO, COLINEAIRE
	};

	public static ArrayList<Circle> pointsInitiaux(int N) {
		ArrayList<Circle> pointsInitiaux = new ArrayList<>();

		for (int i = 0; i < N; i++) {
			pointsInitiaux.add(new Circle(new Random().nextInt(661) + 10, new Random().nextInt(661) + 10, 10, Color.color(Math.random(), Math.random(), Math.random())));
		}

		return pointsInitiaux;
	}

	public static ArrayList<Circle> enveloppe(ArrayList<Circle> points) {
		ArrayList<Circle> pointsRanges = new ArrayList<>(rangerPoints(points));
		//System.out.println(pointsRanges);
		Stack<Circle> pile = new Stack<>();
		pile.push(pointsRanges.get(0));
		pile.push(pointsRanges.get(1));

		for (int i = 2; i < pointsRanges.size(); i++) {
			Circle c = pointsRanges.get(i);	// pi
			Circle b = pile.pop();		// dernier point de la pile
			Circle a = pile.peek();	// dernier point de la pile une fois b retiré (pop). peek ne retire pas le point de la pile

			VECTDIR vd = getVecteurDirection(a, b, c);

			switch (vd) {
				case TRIGO:
					pile.push(b);	// On remet b et on ajoute c
					pile.push(c);	// On remet b avant c pour avoir c en haut de la pile. On sait que b est ok
					break;
				case HORAIRE:
					i--;			// On revient au point précédent et on retire le point b
					break;
				case COLINEAIRE:	// On garde le segment [ac]
					pile.push(c);	// On avance au point suivant et on retire b
					break;
			}
		}

		return new ArrayList<>(pile);
	}

	private static Set<Circle> rangerPoints(ArrayList<Circle> points) {
		final Circle p0 = getPointInitial(points);	// On détermine le point initial qui est le point ayant l'ordonnée minimale

		/* TreeSet est un excellent choix pour stocker une liste dans un ordre rangé */
		TreeSet<Circle> pointsRanges = new TreeSet<>((Circle a, Circle b) -> { // Expression lambda qui permet de comparer les points
			if (a == b || a.equals(b)) {	// Doublons
				return 0;
			}
			/* Calcul des angles les deux expressions sont équivalentes mais je trouve la fonction Math.atan2 plus élégante */
			double angleA = Math.atan2(a.getCenterY() - p0.getCenterY(), a.getCenterX() - p0.getCenterX());
			double angleB = Math.atan2(b.getCenterY() - p0.getCenterY(), b.getCenterX() - p0.getCenterX());

			/*double angleA = Math.acos((a.getCenterX() - p0.getCenterX()) / (Math.sqrt(Math.pow(a.getCenterX() - p0.getCenterX(), 2) + Math.pow(a.getCenterY() - p0.getCenterY(), 2))));
			double angleB = Math.acos((b.getCenterX() - p0.getCenterX()) / (Math.sqrt(Math.pow(b.getCenterX() - p0.getCenterX(), 2) + Math.pow(b.getCenterY() - p0.getCenterY(), 2))));
			System.out.println(angleA1 + " " + angleA + ";" + angleB1 + " " + angleB);*/
			if (angleA < angleB) {
				return -1;
			} else if (angleA > angleB) {
				return 1;
			} else {
				/* distance p0A et p0B */
				double distanceA = Math.sqrt(Math.pow(p0.getCenterX() - a.getCenterX(), 2)
						+ Math.pow(p0.getCenterY() - a.getCenterY(), 2));

				double distanceB = Math.sqrt(Math.pow(p0.getCenterX() - b.getCenterX(), 2)
						+ Math.pow(p0.getCenterY() - b.getCenterY(), 2));

				if (distanceA < distanceB) {
					return -1;
				} else {
					return 1;
				}
			}
		});

		pointsRanges.addAll(points);

		return pointsRanges;
	}

	private static Circle getPointInitial(ArrayList<Circle> points) {
		Circle p0 = points.get(0);

		for (int i = 1; i < points.size(); i++) {
			Circle pi = points.get(i);
			/* On compare tous les points de la liste pour trouver celui qui a une 
			ordonnée la plus basse et une abscisse la plus basse en cas d'égalité */
			if (pi.getCenterY() < p0.getCenterY()
					|| (pi.getCenterY() == p0.getCenterY()
					&& pi.getCenterX() < p0.getCenterX())) {
				p0 = pi;
			}
		}

		//System.out.println("p0:" + p0.getCenterX() + " " + p0.getCenterY());
		return p0;
	}

	private static VECTDIR getVecteurDirection(Circle a, Circle b, Circle c) {
		double produitEnCroix = (b.getCenterX() - a.getCenterX()) * (c.getCenterY() - a.getCenterY())
				- (c.getCenterX() - a.getCenterX()) * (b.getCenterY() - a.getCenterY());
		if (produitEnCroix > 0) {
			return VECTDIR.TRIGO;
		} else if (produitEnCroix < 0) {
			return VECTDIR.HORAIRE;
		} else {
			return VECTDIR.COLINEAIRE;
		}
	}
}
