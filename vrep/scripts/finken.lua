local finken = {}

finkenCore = require('finkenCore')

--save a timestamped log array to a file
--currently only works for data arrays with 3 columns
--@newSuffix and @newDirectoryPath are optional
local function saveLog(newLog, newLogName, newSuffix, newDirectoryPath)
	local myTimeString = os.date("%Y%m%d%H%M%S")
	newDirectoryPath = newDirectoryPath or ""
	newSuffix = newSuffix or ""
	local myLogFile = assert(io.open(newDirectoryPath.."simulation" .. newSuffix .. newLogName .. myTimeString .. ".log", "w"))
	local timestamp, logValue 
	local sortedLogKeys = {}
	for timestamp, logValue in pairs(newLog) do
		table.insert(sortedLogKeys,timestamp)
	end
	table.sort(sortedLogKeys)

	for  _, timestamp in ipairs(sortedLogKeys) do
		logValue = newLog[timestamp]
		myLogFile:write(timestamp, ": ", logValue[1], " ", logValue[2]," ", logValue[3], "\n")
		myLogFile:flush()
	end
	return myLogFile:close()
end

--implement the FINken main class
function finken.init(self)
	local positionLog = {}
	local orientationLog = {}

	--example of a local helper function only for the FINken class
	local function helperSay(textToSay)
		simAddStatusbarMessage(textToSay)
	end

	--example of a new function for the FINken
	--use with care, better write a local function and call it e.g. in self.customRun() 
	function self.helloWorld()
		helperSay("Hello World. Tschieep!")
	end
	
	function self.getArenaPosition()
		local handle_myReference = simGetObjectHandle("Master#")
		local handle_mySelfBase = self.getBaseHandle()
		local myPosition = simGetObjectPosition(handle_mySelfBase, handle_myReference) 
		return myPosition
	end




	--function customRun should be called in the vrep child script in the actuation part
	--put here any custom function that should be called each simulation time step
	function self.customRun()
		local timestamp = math.floor(simGetSimulationTime()*1000)
		positionLog[timestamp] = self.getArenaPosition()
		local baseOrientation = simGetObjectOrientation(self.getBaseHandle(), -1)
		baseOrientation[1] = baseOrientation[1] * (180/math.pi)
		baseOrientation[2] = -1 * baseOrientation[2] * (180/math.pi)
		baseOrientation[3] = -1 * baseOrientation[3] * (180/math.pi)
		orientationLog[timestamp] = baseOrientation
	end
	
	--function customClean should be called in the vrep child scrip in the cleanup part
	--put here any custom function that should be called at the end of the simulation
	function self.customClean()
		saveLog(positionLog, "Position", simGetNameSuffix(nil))
		saveLog(orientationLog, "Orientation", simGetNameSuffix(nil))
	end
	return self
end



function finken.new()
	finkenCore.init()
	return finken.init(finkenCore)
end

return finken
