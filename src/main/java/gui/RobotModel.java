package gui;

import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 * Модель робота, содержащая его координаты, направление и положение цели.
 * Инкапсулирует логику движения робота и уведомляет слушателей об
 * изменении состояния через PropertyChangeSupport. Модель не знает,
 * кто именно на неё подписан: это может быть визуализатор, информационное
 * окно или любой другой наблюдатель
 */
public class RobotModel {
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private volatile double robotX = 100;
    private volatile double robotY = 100;
    private volatile double robotDir = 0;

    private volatile int targetX = 150;
    private volatile int targetY = 100;

    private static final double MAX_VELOCITY = 0.4;
    private static final double DISTANCE_TOLERANCE = 0.5;

    /**
     * Подписывает слушателя на уведомления об изменении состояния модели.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Устанавливает новую целевую точку для робота.
     * После изменения цели все слушатели уведомляются, чтобы интерфейс мог
     * перерисовать и визуализировать новое направление движения
     */
    public void setTargetPosition(Point p) {
        if (p == null) {
            return;
        }

        targetX = p.x;
        targetY = p.y;
        fireStateChanged();
    }
    /**
     * Обновляет состояние робота на один шаг моделирования.
     * Метод вычисляет расстояние до цели, корректирует угловую скорость и
     * линейную скорость, выполняет движение и уведомляет всех слушателей об
     * обновлении состояния
     */
    public void update(double durationMs) {
        double startX = robotX;
        double startY = robotY;
        double startDir = robotDir;

        double dx = targetX - startX;
        double dy = targetY - startY;
        double dist = Math.hypot(dx, dy);

        if (dist < DISTANCE_TOLERANCE) {
            robotX = targetX;
            robotY = targetY;
            fireStateChanged();
            return;
        }

        double velocity = MAX_VELOCITY;
        if (dist < 10.0) {
            velocity = Math.max(0.01, dist * 0.05);
        }

        double radius = computeSignedRadius(startX, startY, startDir, targetX, targetY);

        double angularVelocity;

        if (!Double.isFinite(radius) || Math.abs(radius) > 1e6) {
            angularVelocity = 0.0;
        } else {
            angularVelocity = velocity / radius;
        }

        if (Math.abs(angularVelocity) > 1e-12) {
            double centerX = startX - radius * Math.sin(startDir);
            double centerY = startY + radius * Math.cos(startDir);

            double startAngle = Math.atan2(startY - centerY, startX - centerX);
            double targetAngle = Math.atan2(targetY - centerY, targetX - centerX);

            double sweep = angularVelocity * durationMs;
            double arcDelta = asSignedNormalizedRadians(targetAngle - startAngle);

            boolean willReach =
                    (sweep > 0 && arcDelta >= 0 && arcDelta <= sweep) ||
                            (sweep < 0 && arcDelta <= 0 && arcDelta >= sweep);

            if (willReach) {
                double timeToTarget = arcDelta / angularVelocity;

                moveRobot(velocity, angularVelocity, timeToTarget);

                robotX = targetX;
                robotY = targetY;

                fireStateChanged();
                return;
            }
        }

        moveRobot(velocity, angularVelocity, durationMs);

        fireStateChanged();
    }
    /**
     * Возвращает текущую координату X робота.
     */
    public double getRobotX() {return (int) robotX;
    }

    /**
     * Возвращает текущую координату Y робота.
     */
    public double getRobotY() {
        return (int) robotY;
    }

    /**
     * Возвращает текущий угол поворота робота.
     */
    public double getRobotDir() {
        return robotDir;
    }

    /**
     * Возвращает координату X цели.
     */
    public int getTargetX() {
        return targetX;
    }

    /**
     * Возвращает координату Y цели.
     */
    public int getTargetY() {
        return targetY;
    }

    /**
     * Вычисляет угол от текущей позиции робота до цели.
     */
    public double getAngleToTarget() {
        return angleTo(robotX, robotY, targetX, targetY);
    }

    /**
     * Уведомляет всех подписчиков о том, что состояние модели изменилось.
     */
    private void fireStateChanged() {
        propertyChangeSupport.firePropertyChange("state", null, null);
    }


    /**
     * Вычисляет направление от одной точки к другой и нормализует угол.
     */
    private double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    /**
     * Ограничивает значение заданным диапазоном.
     */
    private double applyLimits(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * Пересчитывает координаты и угол робота на основе линейной и угловой скорости.
     */
    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);

        double newX = robotX + velocity / angularVelocity *
                (Math.sin(robotDir + angularVelocity * duration) -
                        Math.sin(robotDir));
        if (!Double.isFinite(newX)) {
            newX = robotX + velocity * duration * Math.cos(robotDir);
        }
        double newY = robotY - velocity / angularVelocity *
                (Math.cos(robotDir + angularVelocity * duration) -
                        Math.cos(robotDir));
        if (!Double.isFinite(newY)) {
            newY = robotY + velocity * duration * Math.sin(robotDir);
        }
        robotX = newX;
        robotY = newY;
        robotDir = asNormalizedRadians(robotDir + angularVelocity * duration);
    }
    private double computeSignedRadius(double x, double y, double dir, double tx, double ty) {
        double dx = tx - x;
        double dy = ty - y;

        double nx = -Math.sin(dir);
        double ny = Math.cos(dir);

        double denom = dx * nx + dy * ny;

        if (Math.abs(denom) < 1e-9) {
            return Double.POSITIVE_INFINITY;
        }

        return (dx * dx + dy * dy) / (2.0 * denom);
    }
    /**
     * Нормализует угол к диапазону {@code [0, 2π)}.
     */
    private double asNormalizedRadians(double angle) {
        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }
    /**
     * Нормализует угол к диапазону {@code (-π, π]}.
     */
    private double asSignedNormalizedRadians(double angle) {
        angle = asNormalizedRadians(angle);
        if (angle > Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }
}
