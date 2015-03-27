using System;
using System.Windows;

namespace MoveObjectWpf.StickSlip
{
    public static class PointUtil
    {
        public static double getDistance(Point point1, Point point2)
        {
            double x = Math.Abs(point1.X - point2.X);
            double y = Math.Abs(point1.Y - point2.Y);

            return Math.Sqrt((x * x) + (y * y));
        }

        public static Point subtractPoints(Point point1, Point point2)
        {
            return new Point(
                point1.X - point2.X,
                point1.Y - point2.Y);
        }

        public static double getAlphaAngle(Point centerPoint, Point point)
        {
            double a = Math.Abs(point.Y - centerPoint.Y);
            double c = getDistance(centerPoint, point);

            return Math.Asin(a / c) * (180.0 / Math.PI);
        }
    }
}