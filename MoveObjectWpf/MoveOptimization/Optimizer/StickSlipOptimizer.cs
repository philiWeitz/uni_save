using System;
using System.Threading;
using System.Windows;
using log4net;
using MoveObjectWpf.StickSlip;

namespace MoveOptimization.Optimizer
{
    internal enum OptimizationState
    {
        STOPPED,
        MOVE_TO_START,
        MEASURING
    }

    internal class StickSlipOptimizer
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(StickSlipOptimizer));

        private readonly Point startPoint = new Point(20, 50);
        private readonly Point goalPoint = new Point(30, 50);

        private Timer timer;
        private OptimizationState state = OptimizationState.STOPPED;
        private readonly StickSlipControl stickSlipControl = new StickSlipControl();

        public void startOptimization()
        {
            state = OptimizationState.MOVE_TO_START;
            setUnoptimizedMoveProperties();
        }

        public void stopOptimization()
        {
            state = OptimizationState.STOPPED;
            stickSlipControl.stopActuation();
        }

        public void updatePosition(Point? currentPosition)
        {
            if (null == currentPosition || !currentPosition.HasValue)
            {
                stickSlipControl.stopActuation();
            }
            else
            {
                switch (state)
                {
                    case OptimizationState.MOVE_TO_START:
                        moveToStart(currentPosition.Value);
                        break;

                    case OptimizationState.MEASURING:
                        logger.Debug("Measure..");
                        break;

                    default:
                        break;
                }
            }
        }

        private void moveToStart(Point currentPosition)
        {
            double distance = PointUtil.getDistance(currentPosition, startPoint);

            if (distance < 5)
            {
                logger.Debug("Object on start position");
                startMoveForward(currentPosition);
            }
            else
            {
                stickSlipControl.adjustActuation(currentPosition, startPoint);
            }
        }

        private void startMoveForward(Point currentPosition)
        {
            // TODO: set new moving parameter

            stickSlipControl.adjustActuation(currentPosition, goalPoint);
            state = OptimizationState.MEASURING;

            logger.Debug("Starting measurement...");

            TimeSpan waitTime = new TimeSpan(0, 0, 5);
            timer = new Timer(delegate(object s)
            {
                logger.Debug("Stop Optimization");
                // TODO: generate statistics

                // start optimization again
                logger.Debug("Restart Optimization");
                startOptimization();
            }, null, waitTime, new TimeSpan(-1));
        }

        private void setUnoptimizedMoveProperties()
        {
            // TODO: set parameter which will make the object move for sure
        }
    }
}