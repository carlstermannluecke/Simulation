local graphParams = simGetObjectHandle("finkenParams")

function newPIDController(p, i, d)
	local integral = 0
	local previousError = 0

	local lastDesired = nil
	function adjust(error)
		local timeStep = simGetSimulationTimeStep()

		integral = integral + (error * timeStep)
		local derivative = (error - previousError) / timeStep

		if (previousError == 0) then
			derivative = 0
		end
		
		previousError = error

		return (p * error) + (i * integral) + (d * derivative)
	end

	return {
		lastDesired = lastDesired,
		
		adjust = adjust
	}
end

function euclideanDistance(firstObject, secondObject)
	local firstPosition = simGetObjectPosition(firstObject, -1)
	local secondPosition = simGetObjectPosition(secondObject, -1)

	local sum = (firstPosition[1] - secondPosition[1]) ^ 2 
				+ (firstPosition[2] - secondPosition[2]) ^ 2 
				+ (firstPosition[3] - secondPosition[3]) ^ 2

	return math.sqrt(sum)
end

function detect(fromObject, toObjects)
	local distances = {}

	local fromPosition = simGetObjectPosition(fromObject, -1)

	for _, toObject in ipairs(toObjects) do
		table.insert(distances, euclideanDistance(fromObject, toObject))
	end

	return distances
end

function newFinken(object, otherObjects)
	local State = {DEFAULT = "DEFAULT", CRASH = "CRASH", ESCAPE = "ESCAPE"}

	local currentState = State.DEFAULT

	local script = simGetScriptAssociatedWithObject(object)

	local xPIDController = newPIDController(0.5, 0, 0)
	local pitchPIDController = newPIDController(0.2, 0, 0)
	local yPIDController = newPIDController(0.5, 0, 0)
	local rollPIDController = newPIDController(2, 0, 0)
	
	local zPIDController = newPIDController(0.5, 0, 0)
	local throttlePIDController = newPIDController(2, 0, 0)
	
	local maxPitch = 30
	local minPitch = -30

	local maxRoll = 30
	local minRoll = -30

	local previousPosition = simGetObjectPosition(object, -1)
	
	local previousDistance = nil

	local crashDistance = 0.3
	local safetyDistance = 1

	function transformToLocalSystem(object,position)
		local finkenMatrix = simGetObjectMatrix(object,-1)
		local finkenMatrixInverse = simGetInvertedMatrix(finkenMatrix)
		local transformedPosition = simMultiplyVector(finkenMatrixInverse, position)
		return transformedPosition
	end
	
	function move(targetPosition)
		if (not (currentState == State.CRASH)) then
			currentState = State.DEFAULT

			local distances = detect(object, otherObjects)

			for _, distance in ipairs(distances) do
				if (distance < crashDistance) then
					currentState = State.CRASH
					break
				elseif (distance < safetyDistance) then
					currentState = State.ESCAPE
					--targetPosition = previousPosition
					break
				end 
			end
		end

		print(object .. ' ' .. currentState)

		if (currentState == State.DEFAULT or currentState == State.ESCAPE) then
			local timeStep = simGetSimulationTimeStep()
			local currentPosition = simGetObjectPosition(object, -1)
			local currentRelative = transformToLocalSystem(object,currentPosition)
			local targetRelative = transformToLocalSystem(object,targetPosition)
			
			-- Manipulate Z with velocity-thorottle ajustment.

			local targetZ = targetPosition[3]
			local currentZ = currentPosition[3]
			local previousZ = previousPosition[3]

			local velocityZError = zPIDController.adjust(targetZ - currentZ)
			local currentVelocity = (currentZ - previousZ) / timeStep
		
			local throttleError = throttlePIDController.adjust(velocityZError - currentVelocity)
			local throttle = 50 + throttleError / 0.1962
			
			simSetScriptSimulationParameter(script, 'throttle', throttle)
			
			-- Manipulate X with pitch ajustment.
			local targetX = targetRelative[2]
			local currentX = currentRelative[2]
			local lastTargetX = xPIDController.lastDesired or targetX
			
			local velocityXError = xPIDController.adjust(targetX - currentX)
			
			xPIDController.lastDesired = targetX
			local currentVelocityX = (lastTargetX - targetX) / timeStep
			if(math.abs(lastTargetX - targetX) > 0.5) then
				currentVelocityX = velocityXError
				-- wait for next round, when "same" target is used
			end
			local pitchError = pitchPIDController.adjust(velocityXError - currentVelocityX)
			local pitch = math.deg(math.atan(pitchError / throttle))
			if (pitch > maxPitch) then
				pitch = maxPitch
			elseif (pitch < minPitch) then
				pitch = minPitch
			end

			simSetScriptSimulationParameter(script, 'pitch', pitch)

			-- Manipulate Y with roll adjustment.
			local targetY = targetRelative[3]
			local currentY = currentRelative[3]
			local lastTargetY = yPIDController.lastDesired or targetY
			
			local velocityYError = yPIDController.adjust(targetY - currentY)
			yPIDController.lastDesired = targetY
			local currentVelocityY = (lastTargetY - targetY) / timeStep
			if(math.abs(lastTargetY - targetY) > 0.5) then
				currentVelocityY = velocityYError
				-- wait for next round, when "same" target is used
			end
			local rollError = rollPIDController.adjust(velocityYError - currentVelocityY)
			local roll = math.deg(math.atan(rollError / throttle))
			if (roll > maxRoll) then
				roll = maxRoll
			elseif (roll < minRoll) then
				roll = minRoll
			end

			simSetScriptSimulationParameter(script, 'roll', roll)

			-- Update previous position
			if (currentState == State.DEFAULT) then
				previousPosition = currentPosition
			end
		elseif (currentState == State.CRASH) then
			simSetScriptSimulationParameter(script, 'throttle', 0)
		end
	end
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	function randomWalk(distance, height)
		simAddStatusbarMessage(distance)
		local timeStep = simGetSimulationTimeStep()
		local currentPosition = simGetObjectPosition(object, -1)
		local currentRelative = transformToLocalSystem(object,currentPosition)
		
		-- Manipulate Z with velocity-thorottle adjustment.

		local targetZ = height
		local currentZ = currentPosition[3]
		local previousZ = previousPosition[3]

		local velocityZError = zPIDController.adjust(targetZ - currentZ)
		local currentVelocity = (currentZ - previousZ) / timeStep
	
		local throttleError = throttlePIDController.adjust(velocityZError - currentVelocity)
		local throttle = 50 + throttleError
		simAddStatusbarMessage("H"..targetZ.."cH"..currentZ.."pH"..previousZ.."vE"..velocityZError)
		simAddStatusbarMessage("cV"..currentVelocity.."TH"..throttle)
		
		simSetScriptSimulationParameter(script, 'throttle', throttle)
		simSetGraphUserData(graphParams, "TH", throttle)
		-- Manipulate X with pitch ajustment.
		local targetX = distance
		local currentX = 0
		local lastTargetX = xPIDController.lastDesired or targetX
	
		velocityXError = xPIDController.adjust(distance)

		xPIDController.lastDesired = targetX
		local currentVelocityX = (lastTargetX - targetX) / timeStep

		local pitchError = pitchPIDController.adjust(velocityXError - currentVelocityX)
		local pitch = math.deg(math.atan(pitchError / throttle))
		if (pitch > maxPitch) then
			pitch = maxPitch
		elseif (pitch < minPitch) then
			pitch = minPitch
		end

		simSetScriptSimulationParameter(script, 'pitch', pitch)
		simSetGraphUserData(graphParams, "PI", pitch)
		--[[ Manipulate Y with roll adjustment.
		local targetY = targetRelative[3]
		local currentY = currentRelative[3]
		local lastTargetY = yPIDController.lastDesired or targetY
		
		local velocityYError = yPIDController.adjust(targetY - currentY)
		yPIDController.lastDesired = targetY
		local currentVelocityY = (lastTargetY - targetY) / timeStep
		if(math.abs(lastTargetY - targetY) > 0.5) then
			currentVelocityY = velocityYError
			-- wait for next round, when "same" target is used
		end
		local rollError = rollPIDController.adjust(velocityYError - currentVelocityY)
		local roll = math.deg(math.atan(rollError / throttle))
		if (roll > maxRoll) then
			roll = maxRoll
		elseif (roll < minRoll) then
			roll = minRoll
		end

		simSetScriptSimulationParameter(script, 'roll', roll)]]
		simSetGraphUserData(graphParams, "RO", 0)
		local accYaw = simGetScriptSimulationParameter(script, 'yaw')
		
		local rnd = math.random()
		local rndYaw = 0
		if(previousDistance == nil or previousDistance > distance) then
			rndYaw = rnd * 0.2
		else
			rndYaw = rnd * 5
		end
		local yaw = accYaw + rndYaw
		--[[yaw = yaw % 360
		if(yaw >= 180) then
			yaw = yaw - 360
		end]]
		simAddStatusbarMessage("YAW:" .. yaw)
		simAddStatusbarMessage("DIST" .. distance)
		simSetScriptSimulationParameter(script, 'yaw', yaw)
		simSetGraphUserData(graphParams, "YA", yaw)
		previousPosition = currentPosition
		previousDistance = distance
	end
	return {
		move = move,
		randomWalk = randomWalk,
		transformToLocalSystem = transformToLocalSystem
	}
end